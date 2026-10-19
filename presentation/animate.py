#!/usr/bin/env python3
"""Animations and pictures of floorplans in the style of the program's SVG export.

usage:
  animate.py gif RUN_DIR OUT.gif [--frames=N] [--delay=MS] [--pause=MS]
      Animates a run of the program (an output directory with prototype_f.svg
      and the improvements i_*.svg). Each frame shows one improvement of the
      best floorplan, drawn at one pixel per unit on a canvas large enough for
      all frames (light outside of the floorplan, so that it is not mistaken
      for dead space). With --frames, N frames are chosen evenly from the
      improvements (the first and the last are always kept). The frames are
      shown for --delay ms (default 200) and the last one for --pause ms
      (default 5000) before the animation starts again.
  animate.py pause IN.gif OUT.gif [--pause=MS]
      Sets the display time of the last frame of an existing GIF (default
      5000 ms). Only the delay field is changed, the frames stay as they are.
  animate.py picture FLOORPLAN OUT.png --area=PX [--dead=PERCENT]
      Draws a floorplan (an SVG of the program or a CSV file
      "module,x,y,w,h" written by the benchmark runner) with its longer side
      horizontal, scaled so that the area of its modules covers PX pixels,
      so that floorplans of the same benchmark are drawn at the same scale.
      A picture of a floorplan (PNG) is only rotated and scaled; its dead
      space must then be given by --dead.
"""
import csv
import glob
import math
import os
import re
import sys

from PIL import Image, ImageDraw

# colours of the program's SVG export: dead space, modules, module borders; canvas outside of the floorplan
DEAD, MODULE, BORDER, OUTSIDE = (30, 30, 30), (255, 255, 255), (120, 120, 120), (225, 225, 225)
PALETTE = list(DEAD) + list(MODULE) + list(BORDER) + list(OUTSIDE)
RECT = re.compile(r'<rect x="(\d+)" y="(\d+)" width="(\d+)" height="(\d+)" style="fill:rgb\(([\d,]+)\)')


def read_svg(path):
    """Returns the size of the floorplan and its modules (x, y, w, h)."""
    rects = [(int(x), int(y), int(w), int(h), fill) for x, y, w, h, fill in RECT.findall(open(path).read())]
    width, height = rects[0][2], rects[0][3]
    return width, height, [r[:4] for r in rects if r[4] == '255,255,255']


def read_csv(path):
    modules = [tuple(int(v) for v in row[1:5]) for row in csv.reader(open(path)) if row]
    return max(x + w for x, y, w, h in modules), max(y + h for x, y, w, h in modules), modules


def draw(width, height, modules, scale=1.0, canvas=None):
    """Draws the modules (y axis pointing down, as in the SVG export) in the top left corner of the canvas."""
    size = canvas or (width, height)
    image = Image.new('P', (max(1, round(size[0] * scale)), max(1, round(size[1] * scale))), 3)
    image.putpalette(PALETTE)
    pen = ImageDraw.Draw(image)
    pen.rectangle((0, 0, round(width * scale) - 1, round(height * scale) - 1), fill=0)
    for x, y, w, h in modules:
        box = (round(x * scale), round(y * scale), round((x + w) * scale) - 1, round((y + h) * scale) - 1)
        pen.rectangle(box, fill=1, outline=2 if min(w, h) * scale >= 3 else None)
    return image


def save_gif(frames, path, delay, pause):
    durations = [delay] * (len(frames) - 1) + [pause]
    frames[0].save(path, save_all=True, append_images=frames[1:], duration=durations, loop=0, optimize=False)


def gif(run, out, frames=0, delay=200, pause=5000):
    paths = [os.path.join(run, 'prototype_f.svg')] + sorted(glob.glob(os.path.join(run, 'i_*.svg')))
    if frames and len(paths) > frames:
        paths = [paths[round(i * (len(paths) - 1) / (frames - 1))] for i in range(frames)]
    floorplans = [read_svg(p) for p in paths]
    canvas = (max(f[0] for f in floorplans), max(f[1] for f in floorplans))
    images = [draw(*f, canvas=canvas) for f in floorplans]
    save_gif(images, out, delay, pause)
    print('%s: %d frames, canvas %dx%d, first floorplan %dx%d, last %dx%d' % (
        out, len(images), canvas[0], canvas[1], floorplans[0][0], floorplans[0][1], floorplans[-1][0], floorplans[-1][1]))


def gif_delays(data):
    """Returns the offsets of the delay fields of the graphic control extensions of a GIF."""
    if data[:6] not in (b'GIF87a', b'GIF89a'):
        raise ValueError('not a GIF file')
    pos = 13 + (3 * (2 << (data[10] & 7)) if data[10] & 0x80 else 0)
    delays = []

    def skip_blocks(pos):
        while data[pos]:
            pos += data[pos] + 1
        return pos + 1

    while data[pos] != 0x3B:
        if data[pos] == 0x21:
            if data[pos + 1] == 0xF9:
                delays.append(pos + 4)
            pos = skip_blocks(pos + 2)
        elif data[pos] == 0x2C:
            flags = data[pos + 9]
            pos += 10 + (3 * (2 << (flags & 7)) if flags & 0x80 else 0)
            pos = skip_blocks(pos + 1)
        else:
            raise ValueError('unexpected block 0x%02x at %d' % (data[pos], pos))
    return delays


def pause_gif(path, out, pause=5000):
    data = bytearray(open(path, 'rb').read())
    last = gif_delays(data)[-1]
    data[last:last + 2] = (pause // 10).to_bytes(2, 'little')
    open(out, 'wb').write(data)
    print('%s: last frame shown for %d ms' % (out, pause))


def picture(path, out, area, dead=None):
    if path.endswith('.svg') or path.endswith('.csv'):
        width, height, modules = read_svg(path) if path.endswith('.svg') else read_csv(path)
        if height > width:
            modules = [(y, x, h, w) for x, y, w, h in modules]
            width, height = height, width
        used = sum(w * h for _, _, w, h in modules)
        scale = math.sqrt(area / used)
        # drawn at a high resolution and scaled down, like a rasterised vector picture
        fine = 2400.0 / width
        image = draw(width, height, modules, fine).convert('RGB')
        image = image.resize((round(width * scale), round(height * scale)), Image.Resampling.LANCZOS)
    else:
        # the whole picture is the floorplan, so its area is that of the modules plus the dead space
        image = Image.open(path).convert('RGB')
        if image.height > image.width:
            image = image.transpose(Image.Transpose.ROTATE_90)
        scale = math.sqrt(area * (1 + dead / 100.0) / (image.width * image.height))
        image = image.resize((round(image.width * scale), round(image.height * scale)), Image.Resampling.LANCZOS)
    image.save(out)
    print('%s: %dx%d' % (out, image.width, image.height))


def main():
    args = [a for a in sys.argv[1:] if not a.startswith('--')]
    opts = dict(a[2:].split('=', 1) for a in sys.argv[1:] if a.startswith('--'))
    if args[0] == 'gif':
        gif(args[1], args[2], int(opts.get('frames', 0)), int(opts.get('delay', 200)), int(opts.get('pause', 5000)))
    elif args[0] == 'pause':
        pause_gif(args[1], args[2], int(opts.get('pause', 5000)))
    elif args[0] == 'picture':
        picture(args[1], args[2], float(opts['area']), float(opts['dead']) if 'dead' in opts else None)
    else:
        raise SystemExit(__doc__)


if __name__ == '__main__':
    main()
