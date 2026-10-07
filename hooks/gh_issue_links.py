"""MkDocs hook: turn bare #N references into links to GitHub issues (#668)."""
import re

REPO_URL = "https://github.com/SPARQL-Anything/sparql.anything/issues/"

# Fenced code blocks and inline code spans are left untouched.
_CODE = re.compile(r"(^```.*?^```|^~~~.*?^~~~|`[^`\n]*`)", re.M | re.S)
# #N not preceded by a word char, &, /, [, # (entities, URLs, anchors, existing links)
# and not followed by a word char or ] (avoids hex colours, already-linked text).
_REF = re.compile(r"(?<![\w&/\[#])#(\d+)\b(?![\]\w])")


def _link(text):
    return _REF.sub(lambda m: f"[#{m.group(1)}]({REPO_URL}{m.group(1)})", text)


def on_page_markdown(markdown, **kwargs):
    parts = _CODE.split(markdown)
    # split() with one capture group: even indices = prose, odd = code
    return "".join(p if i % 2 else _link(p) for i, p in enumerate(parts))