e2e_test:
	python .\e2e_test\btc_trading_test.py

build:
	docker-compose build

run:
	docker-compose up -d
