import requests
import time
from decimal import Decimal

BASE_URL = "http://localhost:8080"

def test_create_user():
    response = requests.post(f"{BASE_URL}/users", params={"name": "Test User", "email": "test@example.com"})
    assert response.status_code == 200, f"Failed to create user. Status code: {response.status_code}"
    user = response.json()
    assert user["name"] == "Test User"
    assert user["email"] == "test@example.com"
    assert Decimal(str(user["usdBalance"])) == Decimal("1000")
    assert Decimal(str(user["btcBalance"])) == Decimal("0")
    print(f"User created successfully. User ID: {user['id']}")
    return user["id"]

def test_delete_user(user_id):
    response = requests.delete(f"{BASE_URL}/users/{user_id}")
    print(f"Delete user response: {response.text}")  # 添加這行來打印響應內容
    assert response.status_code == 200, f"Failed to delete user. Status code: {response.status_code}, Response: {response.text}"
    print(f"Successfully deleted user with ID: {user_id}")

    # Verify that the user is actually deleted
    response = requests.get(f"{BASE_URL}/transactions/{user_id}")
    assert response.status_code == 404, f"User still exists. Status code: {response.status_code}, Response: {response.text}"
    print("Verified user deletion.")

def test_buy_btc(user_id):
    btc_amount = "0.1"
    response = requests.post(f"{BASE_URL}/transactions/buy", params={"userId": user_id, "btcAmount": btc_amount})
    assert response.status_code == 200, f"Failed to buy BTC. Status code: {response.status_code}"
    transaction = response.json()
    assert transaction["type"] == "BUY"
    assert abs(Decimal(str(transaction["btcAmount"])) - Decimal(btc_amount)) < Decimal('0.000001')
    print(f"Successfully bought {btc_amount} BTC at price {Decimal(str(transaction['btcPrice']))}")

def test_sell_btc(user_id):
    btc_amount = "0.05"
    response = requests.post(f"{BASE_URL}/transactions/sell", params={"userId": user_id, "btcAmount": btc_amount})
    assert response.status_code == 200, f"Failed to sell BTC. Status code: {response.status_code}"
    transaction = response.json()
    assert transaction["type"] == "SELL"
    assert abs(Decimal(str(transaction["btcAmount"])) - Decimal(btc_amount)) < Decimal('0.000001')
    print(f"Successfully sold {btc_amount} BTC at price {Decimal(str(transaction['btcPrice']))}")

def test_get_user_transactions(user_id):
    response = requests.get(f"{BASE_URL}/transactions/{user_id}")
    assert response.status_code == 200, f"Failed to get user transactions. Status code: {response.status_code}"
    transactions = response.json()
    assert len(transactions) == 2
    assert transactions[0]["type"] == "SELL"
    assert transactions[1]["type"] == "BUY"
    assert abs(Decimal(str(transactions[0]["btcAmount"])) - Decimal("0.05")) < Decimal('0.000001')
    assert abs(Decimal(str(transactions[1]["btcAmount"])) - Decimal("0.1")) < Decimal('0.000001')
    print("Successfully retrieved user transactions.")

def test_btc_price():
    initial_price = Decimal(str(requests.get(f"{BASE_URL}/transactions/btc/price").json()))
    print(f"Initial price: {initial_price}")
    
    assert Decimal("100") <= initial_price <= Decimal("460")

    wait_time = 10
    print(f"Waiting for {wait_time} seconds...")
    time.sleep(wait_time)

    new_price = Decimal(str(requests.get(f"{BASE_URL}/transactions/btc/price").json()))
    print(f"New price after {wait_time} seconds: {new_price}")

    assert Decimal("100") <= new_price <= Decimal("460")
    assert new_price != initial_price

    expected_change = Decimal("10") * (wait_time // 5)
    actual_change = abs(new_price - initial_price)
    assert abs(actual_change - expected_change) <= Decimal("1")

    print("BTC price test passed successfully.")

def run_tests():
    try:
        print("Creating user...")
        user_id = test_create_user()

        print("Testing BTC price...")
        test_btc_price()

        print("Buying BTC...")
        test_buy_btc(user_id)

        print("Selling BTC...")
        test_sell_btc(user_id)

        print("Getting user transactions...")
        test_get_user_transactions(user_id)
        
        print("Deleting user...")
        test_delete_user(user_id)

        print("All tests passed successfully!")
    except AssertionError as e:
        print(f"Test failed: {str(e)}")
    except Exception as e:
        print(f"An error occurred: {str(e)}")

if __name__ == "__main__":
    run_tests()