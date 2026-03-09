# Simple Makefile for slide generation (Option 1: Pandoc Reveal.js)

# Paths are relative to this Makefile (docs/jBehave)
REVEAL_HTML=jbehave_basics.html
PPTX=JBehave_Basics_Presentation_pandoc.pptx
PPTX_PANDOC=JBehave_Basics_Presentation_pandoc.pptx
MD=JBehave_Basics_Presentation.md
TITLE=JBehave Basics & Applied Overview
REVEAL_THEME=white
REVEAL_JS_URL=https://cdnjs.cloudflare.com/ajax/libs/reveal.js/5.0.4
LANG=en
HIGHLIGHT_STYLE=tango

# Python generator settings
VENV=.venv
PGEN=generate_pptx.py
REQ=requirements.txt
PYTHON=$(VENV)/bin/python
PIP=$(VENV)/bin/pip

# Ensure docs directory exists
prepare:
	@mkdir -p ./

# Generate Reveal.js HTML deck via pandoc
slides-html: prepare
	@echo 'Generating Reveal.js HTML via Pandoc from $(MD)...'
	@pandoc $(MD) -t revealjs -s -o $(REVEAL_HTML) --slide-level=1 \
		-M lang=$(LANG) --metadata title="$(TITLE)" --variable theme=$(REVEAL_THEME) \
		--variable revealjs-url=$(REVEAL_JS_URL) --highlight-style=$(HIGHLIGHT_STYLE) || { echo 'Pandoc failed.'; exit 1; }
	@test -f $(REVEAL_HTML) && echo 'HTML deck ready: $(REVEAL_HTML)'

# Verbose debug generation
slides-html-debug: prepare
	@echo 'Debug: Generating Reveal.js HTML with verbose output...'
	@pandoc $(MD) -t revealjs -s -o $(REVEAL_HTML) --slide-level=1 \
		-M lang=$(LANG) --metadata title="$(TITLE)" --variable theme=$(REVEAL_THEME) \
		--variable revealjs-url=$(REVEAL_JS_URL) --highlight-style=$(HIGHLIGHT_STYLE) --verbose 2> /tmp/pandoc_debug.log || { echo 'Pandoc failed.'; exit 1; }
	@echo 'Debug log saved to /tmp/pandoc_debug.log'
	@test -f $(REVEAL_HTML) && echo 'HTML deck ready: $(REVEAL_HTML)'

# Generate PPTX from markdown
slides-pptx: prepare
	@echo 'Generating PPTX via Pandoc from $(MD)...'
	@pandoc $(MD) -o $(PPTX) --slide-level=1 -M lang=$(LANG) --metadata title="$(TITLE)" --highlight-style=$(HIGHLIGHT_STYLE) || { echo 'Pandoc failed.'; exit 1; }
	@test -f $(PPTX) && echo 'PPTX created: $(PPTX)'

#------------- New: pandoc PPTX target -------------
pptx-pandoc: prepare check-pandoc
	@echo 'Generating PPTX via Pandoc to $(PPTX_PANDOC)...'
	@pandoc $(MD) -o $(PPTX_PANDOC) --slide-level=1 -M lang=$(LANG) --metadata title="$(TITLE)" --highlight-style=$(HIGHLIGHT_STYLE) || { echo 'Pandoc failed.'; exit 1; }
	@test -f $(PPTX_PANDOC) && echo 'Pandoc PPTX created: $(PPTX_PANDOC)'

# --- New python-pptx based workflow ---
# Create or update virtualenv and install requirements
venv:
	@echo 'Setting up virtualenv in $(VENV) (if missing) and installing requirements...'
	@python3 -m venv $(VENV) || true
	@$(PYTHON) -m pip install --upgrade pip setuptools wheel
	@$(PIP) install -r $(REQ)

# Generate PPTX using the python generator script (uses python-pptx)
pptx-python: venv
	@echo 'Generating PPTX via python generator ($(PGEN))...'
	@$(PYTHON) $(PGEN)
	@echo 'Python PPTX generation complete.'

# Default pptx target prefers python generator
pptx: pptx-python
	@echo 'pptx target finished.'

# Create a simple PPTX template programmatically (used by generator if no template provided)
create-template: venv
	@echo 'Creating PPTX template...'
	@$(PYTHON) create_template.py
	@echo 'Template created (docs/jBehave/pptx_template.pptx)'

# Compact PPTX: fewer bullets per slide and smaller fonts
pptx-compact: create-template venv
	@echo 'Generating compact PPTX (fewer bullets, smaller fonts) using template...'
	@MAX_BULLETS=4 TEXT_FONT_SIZE=14 CODE_FONT_SIZE=10 TEMPLATE_PATH=pptx_template.pptx $(PYTHON) $(PGEN)
	@echo 'Compact PPTX generation complete.'

# Open the HTML deck (macOS)
open-html:
	@open $(REVEAL_HTML)

# Verify pandoc installation
check-pandoc:
	@which pandoc || echo 'pandoc not found in PATH'
	@pandoc --version | head -n 1

clean:
	@rm -f $(REVEAL_HTML) $(PPTX) $(PPTX_PANDOC) pptx_template.pptx context.json JBehave_Basics_Presentation.pptx
	@echo 'Cleaned generated artifacts.'

.PHONY: prepare slides-html slides-html-debug slides-pptx open-html clean check-pandoc venv pptx-python pptx pptx-pandoc create-template
