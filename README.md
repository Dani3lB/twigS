# twigS

Dependency free, twig 3.x compatible template engine for Scala.

Currently, in development.

## Implemented twig features
| Tags       |     | Tags     |     |
|------------|-----|----------|-----|
| apply      |     | from     |     |
| autoescape |     | if       | X   |
| block      |     | import   |     |
| cache      |     | include  |     |
| deprecated |     | macro    |     |
| do         |     | sandbox  |     |
| embed      |     | set      |     |
| extends    |     | use      |     |
| flush      |     | verbatim |     |
| for        |     | with     |     |

| Filters          |     | Filters          |     |
|------------------|-----|------------------|-----|
| bs               |     | keys             |     |
| batch            |     | language_name    |     |
| capitalize       |     | last             |     |
| column           |     | length           |     |
| convert_encoding |     | locale_name      |     |
| country_name     |     | lower            |     |
| currency_name    |     | map              |     |
| currency_symbol  |     | markdown_to_html |     |
| data_uri         |     | merge            |     |
| date             |     | nl2br            |     |
| date_modify      |     | number_format    |     |
| default          |     | raw              |     |
| escape           |     | reduce           |     |
| filter           |     | replace          |     |
| first            |     | reverse          |     |
| format           |     | round            |     |
| format_currency  |     | slice            |     |
| format_date      |     | slug             |     |
| format_datetime  |     | sort             |     |
| format_number    |     | spaceless        |     |
| format_time      |     | split            |     |
| html_to_markdown |     | striptags        |     |
| inky_to_html     |     | timezone_name    |     |
| inline_css       |     | title            |     |
| join             | X   | trim             |     |
| json_encode      |     | u                |     |
| upper            |     | url_encode       |     |

| Functions         |     | Functions            |     |
|-------------------|-----|----------------------|-----|
| attribute         |     | language_names       |     |
| block             |     | locale_names         |     |
| constant          |     | max                  |     |
| country_names     |     | min                  |     |
| country_timezones |     | parent               |     |
| currency_names    |     | random               |     |
| cycle             |     | range                | X   |
| date              |     | script_names         |     |
| dump              |     | source               |     |
| html_classes      |     | template_from_string |     |
| include           |     | timezone_names       |     |
