PDFVIEWER = evince
name = show
PDFLATEX = pdflatex
outputdir = temp

all: show

show: pdf
	$(PDFVIEWER) $(name).pdf

pdf: $(name).tex
	$(PDFLATEX) -output-directory $(outputdir) $(name).tex
	$(PDFLATEX) -output-directory $(outputdir) $(name).tex
	cp $(outputdir)/$(name).pdf $(name).pdf

build: pdf

clean:
	rm -f $(outputdir)/*.aux
	rm -f $(outputdir)/*.log
	rm -f $(outputdir)/*.pdf
	rm -f $(outputdir)/*.sty
