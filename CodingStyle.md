# Contract style #

Contracts can be written on one line if there is only one clause and it fits in the, in which case braces should be omitted around the expression.

**Good:**
```
@Invariant("true")
```

**Bad:**
```
@Invariant({"true"})
```

Alternatively, if the clause is long, it can be written as a block (see below), and the braces may be omitted.


Multiline contracts should be indented as would a code block.

**Good:**
```
@Invariant({
  "true",
  "..."
})
```

**Bad:**
```
@Invariant({ "true",
  "..." })
```

Clauses that span multiple lines should be broken down after the concatenating `+` and indented as a continuation line:

```
@Invariant({
  "... a long long ..." +
      "... invariant ..."
})
```


# Cofoja style #

Cofoja follows Google internal style conventions, which are not public but are roughly equivalent to the standard Java guidelines, with 2-space indentation and 4-space line continuation.

Also, the following restrictions apply specifically to Cofoja, and should be honored for consistency:
  * Line breaks occur _after_ an assignment operator, not before.
  * Only `/**/` comments are used, even if they fit on one line.
  * Comments are not required to make up full sentences but should be capitalized and end with a proper punctuation.
  * End-of-line comments are discouraged but should they appear on consecutive lines, they should be lined-up properly.
  * Line length is 80 characters.


# Emacs configuration #

The following C style tries to approximate the Google style. Please keep in mind that this is only a _hint_ and that actual practice wins over any automated tool.

```
(c-add-style
 "google-java"
 '((indent-tabs-mode . nil)
   (c-basic-offset . 2)
   (c-comment-only-line-offset . (0 . 0))
   ;; the following preserves Javadoc starter lines
   (c-offsets-alist . ((inline-open . 0)
		       (topmost-intro-cont    . +)
		       (statement-block-intro . +)
		       (knr-argdecl-intro     . 5)
		       (substatement-open     . +)
		       (substatement-label    . +)
		       (case-label            . +)
		       (label                 . +)
		       (statement-case-open   . +)
		       (statement-cont        . ++)
		       (arglist-intro  . c-lineup-arglist-intro-after-paren)
		       (arglist-close  . c-lineup-arglist)
		       (access-label   . 0)
		       (inher-intro    . ++)
		       (inher-cont     . ++)
		       (func-decl-cont . ++)))))
```