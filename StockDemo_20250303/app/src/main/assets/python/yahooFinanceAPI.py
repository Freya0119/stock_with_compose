import yfinance as yf
import requests
import time
import sys

# part3
stockId = '2330'

# without_yfinance

trans = '%Y-%m-%d'

timeStr = '2025-03-01'
timeStr2 = '2025-03-10'

stamp = time.strptime(timeStr, trans)
timeFloat = int(time.mktime(stamp))

stamp2 = time.strptime(timeStr2, trans)
timeFloat2 = int(time.mktime(stamp2))

interval = '1d'

url = f'https://query1.finance.yahoo.com/v8/finance/chart/{stockId}.TW?period1={timeFloat}&period2={timeFloat2}&interval={interval}&events=history&=hP2rOschxO0'
headers = { 'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36' }
response = requests.get(url, headers = headers)

# with_yfinance

# stock = yf.Ticker('2330.TW')
# his = stock.history(period = '5d', interval = interval, start = timeStr, end = timeStr2)

def get_stock_price(stockId):
    return requests.get(url, headers=headers).text

if __name__ == '__main__':
    stock = sys.argv[1]
    print(f'Stock from kotlin: {stock}')