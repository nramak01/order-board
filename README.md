# order-board
Display to users the demand for silver bars there is on the market.

# Application Startup : 
Run the Java Class - com.silverbars.marketplace.OrderBoardApplication

The Json files are located under src/main/resources

# Curl for order registration: 
curl -d "@data1.json" -X POST http://localhost:7000/order/register

# Curl for cancelling order: 
curl -d "@data1.json" -X POST http://localhost:7000/order/cancel

# Curl to retreive the status of current live orders: 
curl -X GET http://localhost:7000/order/live-orders

For Testing various json orders,
curl -d "@data1.json" -X POST http://localhost:7000/order/register

curl -d "@data2.json" -X POST http://localhost:7000/order/register

curl -d "@data3.json" -X POST http://localhost:7000/order/register

curl -d "@data4.json" -X POST http://localhost:7000/order/register

curl -d "@data5.json" -X POST http://localhost:7000/order/register

curl -d "@data6.json" -X POST http://localhost:7000/order/register


