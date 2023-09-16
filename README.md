# FIDE Ratings Scraper CLI

A CLI tool built with [scala-cli](https://scala-cli.virtuslab.org/) to fetch chess
ratings as JSON from the [FIDE Ratings Website](https://ratings.fide.com/).

## Requirements

- [scala-cli](https://scala-cli.virtuslab.org/install/)

## Usage

Run the CLI tool with the FIDE identifier for whom you want to get the chess ratings for:

```bash
scala-cli run FideRatingsScraper.scala -- <fide id>
```
