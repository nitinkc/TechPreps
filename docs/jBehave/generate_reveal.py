#!/usr/bin/env python3
import re, html, sys, pathlib
BASE = pathlib.Path(__file__).resolve().parent
print(f'[generate_reveal] base={BASE}')
MD_FILE = BASE / 'JBehave_Basics_Presentation.md'
OUT_FILE = BASE / 'jbehave_basics.html'
CUSTOM_CSS = BASE / 'reveal_custom.css'
print(f'[generate_reveal] md={MD_FILE} out={OUT_FILE} css={CUSTOM_CSS}')
text = MD_FILE.read_text(encoding='utf-8').splitlines()
slides_raw = []
current = []
code_block = False
for line in text:
    if line.startswith('# ') and not code_block:
        if current:
            slides_raw.append('\n'.join(current).strip())
            current = []
        current.append(line)
        continue
    if line.strip() == '---' and not code_block:
        if current:
            slides_raw.append('\n'.join(current).strip())
            current = []
        continue
    if line.startswith('```'):
        code_block = not code_block
        current.append(line)
        continue
    current.append(line)
if current:
    slides_raw.append('\n'.join(current).strip())

# Heuristics for splitting: max chars or max items per slide
MAX_CHARS = 400
MAX_ITEMS = 4

html_slides = []
for raw in slides_raw:
    lines = raw.split('\n')
    heading = ''
    body_lines = []
    if lines and lines[0].startswith('# '):
        heading = html.escape(lines[0][2:].strip())
        body_lines = lines[1:]
    else:
        body_lines = lines
    # Convert body into a list of renderable blocks (text or code blocks)
    blocks = []
    in_code = False
    code_buffer = []
    for ln in body_lines:
        if ln.startswith('```'):
            if in_code:
                blocks.append({'type': 'code', 'text': '\n'.join(code_buffer)})
                code_buffer = []
                in_code = False
            else:
                in_code = True
            continue
        if in_code:
            code_buffer.append(ln)
            continue
        stripped = ln.strip()
        if not stripped:
            continue
        # Treat list lines and paragraphs uniformly as text blocks
        blocks.append({'type': 'text', 'text': stripped})
    # if code block left open, flush
    if code_buffer:
        blocks.append({'type': 'code', 'text': '\n'.join(code_buffer)})

    # Now split blocks into one or more slides using heuristics
    cur_blocks = []
    cur_chars = 0
    for b in blocks:
        b_len = len(b['text'])
        # if adding this block exceeds thresholds and current is non-empty, flush current as a slide
        if (cur_blocks and (cur_chars + b_len > MAX_CHARS or len(cur_blocks) >= MAX_ITEMS)):
            # render current slide
            out_html = []
            out_html.append('<section>')
            if heading:
                out_html.append(f'<h2>{heading}</h2>')
                heading = ''  # only show heading on first split
            for cb in cur_blocks:
                if cb['type'] == 'code':
                    out_html.append('<pre><code>')
                    out_html.append(html.escape(cb['text']))
                    out_html.append('</code></pre>')
                else:
                    if cb['text'].startswith('- '):
                        out_html.append(f'<p class="fragment">{html.escape(cb["text"][2:].strip())}</p>')
                    else:
                        out_html.append(f'<p class="fragment">{html.escape(cb["text"])}</p>')
            out_html.append('</section>')
            # mark this slide as compact and append a more-indicator
            compact_html = out_html[0].replace('<section>', '<section class="compact">') + '\n'.join(out_html[1:-1]) + '\n<div class="more-indicator"></div>' + out_html[-1]
            html_slides.append(compact_html)
            # start new
            cur_blocks = [b]
            cur_chars = b_len
        else:
            cur_blocks.append(b)
            cur_chars += b_len
    # flush remaining
    if cur_blocks:
        out_html = []
        out_html.append('<section>')
        if heading:
            out_html.append(f'<h2>{heading}</h2>')
        for cb in cur_blocks:
            if cb['type'] == 'code':
                out_html.append('<pre><code>')
                out_html.append(html.escape(cb['text']))
                out_html.append('</code></pre>')
            else:
                if cb['text'].startswith('- '):
                    out_html.append(f'<p class="fragment">{html.escape(cb["text"][2:].strip())}</p>')
                else:
                    out_html.append(f'<p class="fragment">{html.escape(cb["text"])}</p>')
        out_html.append('</section>')
        slide_html = '\n'.join(out_html)
        # if we split earlier (i.e., more than one slide for this original), make subsequent ones compact
        if len(html_slides) > 0 and ('<h2>' not in slide_html or '<section class=' in slide_html):
            slide_html = slide_html.replace('<section>', '<section class="compact">', 1)
            # append more-indicator for compact
            slide_html = slide_html.replace('</section>', '\n<div class="more-indicator"></div>\n</section>', 1)
        # Check final slide length, and mark as compact if it exceeds MAX_CHARS
        if len(slide_html) > MAX_CHARS and 'class="compact"' not in slide_html:
            slide_html = slide_html.replace('<section>', '<section class="compact">', 1)
            slide_html = slide_html.replace('</section>', '\n<div class="more-indicator"></div>\n</section>', 1)
        html_slides.append(slide_html)

# Build final HTML
css_include = f"<link rel='stylesheet' href='./{CUSTOM_CSS.name}'/>" if CUSTOM_CSS.exists() else ''
skeleton = f"""<!DOCTYPE html>
<html>
<head>
  <meta charset='utf-8'/>
  <title>JBehave Basics & Applied Overview</title>
  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/reveal.min.css'/>
  <link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/theme/white.min.css' id='theme'/>
  {css_include}
  <style>
    code {{ font-size: 0.8em; white-space: pre-wrap; }}
    .reveal section p {{ font-size: 0.95em; }}
    ul {{ font-size: 0.95em; }}
    .reveal .fragment {{ transition: opacity 300ms ease, transform 300ms ease; }}
  </style>
</head>
<body>
<div class='reveal'>
  <div class='slides'>
    {''.join(html_slides)}
  </div>
</div>
<script src='https://cdnjs.cloudflare.com/ajax/libs/reveal.js/4.4.0/reveal.min.js'></script>
<script>
  Reveal.initialize({{
    hash: true,
    slideNumber: true,
    transition: 'convex',
    backgroundTransition: 'fade',
    fragments: true,
    keyboard: true,
    fragments: {{ sync: true }}
  }});
</script>
</body>
</html>
"""
OUT_FILE.parent.mkdir(parents=True, exist_ok=True)
OUT_FILE.write_text(skeleton, encoding='utf-8')
print(f'Generated {OUT_FILE} with {len(html_slides)} slides')
