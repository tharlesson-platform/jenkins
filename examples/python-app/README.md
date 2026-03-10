# python-app

Exemplo minimo em Python para validacao de CI no Jenkins.

## Execucao local

```bash
python -m venv .venv
. .venv/bin/activate
pip install -r requirements-dev.txt
pip install .
pytest -q
python -m sample_app.main
```
