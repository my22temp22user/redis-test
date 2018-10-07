# redis-test

## Background
Credit card service is deployed in amazon EC2 cloud (host ec2-18-219-41-183.us-east-2.compute.amazonaws.com).
It is behind nginx proxy that listens to port 80.

## Usage

* Save credit card and get token
    * curl -X POST -H "Content-Type: application/json" http://ec2-18-219-41-183.us-east-2.compute.amazonaws.com/creditcard -d '{"credit-card":"1234-5678-9101- 1121"}'

* Get credit number by token ($token)
    * curl -X GET http://ec2-18-219-41-183.us-east-2.compute.amazonaws.com/creditcard/$token

