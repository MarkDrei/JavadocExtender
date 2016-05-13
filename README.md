# JavadocExtender

This repository contains eclipse plugins.

This project aims to extend the functionality that javadoc
offers inside eclipse. The user shall be able to add new
tags (like these "@return" elements) and define valid values
behind this tag.

Features:
- autocompletion of tags and valid values
- Upon compile, check whether the given element is valid and if not, report it
- Navigation to the definition of the referenced elements
- Extension Point at which providers can declare the new tags, elements and (optionally) navigation targets