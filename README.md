# Java Multithreaded Web Crawler

This is a simple multithreaded web crawler built in Java as part of a university class assignment. It recursively navigates through a website starting from a defined seed URL, extracting data that matches specified patterns using regular expressions.

The architecture is designed for flexibility, performance, and ease of extension. While the crawler is fully functional and successfully parses through pages, its stopping condition is currently basic and can be improved in future versions for more precise control.

---

## 🌐 Overview

- Crawls websites starting from a user-defined seed URL.
- Extracts targeted information using customizable regex patterns.
- Uses multithreading to improve crawling performance.
- Designed for easy adaptability to new sites and data formats.

---

## ⚙️ Features

- **Multithreaded Architecture**: Crawls multiple pages in parallel for efficient performance.
- **Pattern Matching with Regex**: Finds information based on a set of regular expressions.
- **Easy Pattern Configuration**: Patterns are stored in an `enum` class that can be extended freely—just add a new enum constant with the desired regex.
- **Adaptable Seed URL**: The crawler uses a single configurable constant for the seed URL, replaced with a placeholder in the public repository to protect privacy.
- **Site-agnostic Setup**: Easily adaptable to new sites by updating the seed and patterns.
