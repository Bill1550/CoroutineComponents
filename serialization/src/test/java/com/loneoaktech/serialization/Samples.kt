package com.loneoaktech.serialization

object Samples {

  val oneItemOrder = """{
  "title": "103 - Main Dining Room",
  "state": "open",
  "id": "04HQ28Y7NGWD2",
  "device": {
    "id": "3fb3e85c-608f-4d6e-8f64-167337663339"
  },
  "currency": "USD",
  "isVat": false,
  "groupLineItems": true,
  "testMode": false,
  "createdTime": 1713537904455,
  "clientCreatedTime": 1713537904455,
  "taxRemoved": false,
  "manualTransaction": false,
  "total": 1058,
  "employee": {
    "id": "0KFCCZY3NJAFY"
  },
  "orderType": {
    "id": "WC1FGSYABMJKM",
    "isDefault": true,
    "isDeleted": false,
    "label": "Dine-in",
    "labelKey": "com.clover.order.type.dine_in",
    "taxable": true
  },
  "lineItems": {
    "elements": [
      {
        "id": "SAV3HNT7Q5NY6",
        "item": {
          "id": "55TM5VN2HNQM4"
        },
        "createdTime": 1713537921086,
        "binName": "",
        "userData": null,
        "itemCode": null,
        "price": 995,
        "alternateName": null,
        "name": "Hamburger",
        "exchanged": false,
        "refunded": false,
        "printed": false,
        "isRevenue": true,
        "colorCode": null,
        "taxRates": {
          "elements": [
            {
              "id": "68TVKPTQBXP3A",
              "name": "CT State Sales Tax",
              "rate": 635000,
              "taxAmount": 0,
              "isDefault": true,
              "taxType": null
            }
          ]
        }
      }
    ]
  }
}"""

  val threeItemOrder = """{
  "href": "https://dev1.dev.clover.com/v3/merchants/4G5KV3R6WKGD5/orders/J05ZAK6CSG90P",
  "id": "J05ZAK6CSG90P",
  "currency": "USD",
  "employee": {
    "href": "https://dev1.dev.clover.com/v3/merchants/4G5KV3R6WKGD5/employees/0KFCCZY3NJAFY",
    "id": "0KFCCZY3NJAFY",
    "name": "Bill",
    "nickname": "Hart",
    "customId": "Boss",
    "role": "ADMIN",
    "orders": {
      "href": "https://dev1.dev.clover.com/v3/merchants/4G5KV3R6WKGD5/employees/0KFCCZY3NJAFY/orders"
    }
  },
  "total": 2037,
  "paymentState": "OPEN",
  "title": "101 - Main Dining Room",
  "orderType": {
    "id": "WC1FGSYABMJKM",
    "labelKey": "com.clover.order.type.dine_in",
    "label": "Dine-in",
    "taxable": true,
    "isDefault": true,
    "filterCategories": false,
    "isHidden": false,
    "fee": 0,
    "minOrderAmount": 0,
    "maxOrderAmount": 0,
    "maxRadius": 0,
    "avgOrderTime": 0,
    "hoursAvailable": "BUSINESS",
    "isDeleted": false,
    "systemOrderTypeId": "DINE-IN-TYPE"
  },
  "taxRemoved": false,
  "isVat": false,
  "state": "open",
  "manualTransaction": false,
  "groupLineItems": true,
  "testMode": false,
  "createdTime": 1713472326000,
  "clientCreatedTime": 1713472326000,
  "modifiedTime": 1713537285000,
  "lineItems": {
    "elements": [
      {
        "id": "0HZVYK48V91BA",
        "orderRef": {
          "id": "J05ZAK6CSG90P"
        },
        "item": {
          "id": "55TM5VN2HNQM4"
        },
        "name": "Hamburger",
        "price": 995,
        "printed": false,
        "binName": "",
        "createdTime": 1713472344000,
        "orderClientCreatedTime": 1713472326000,
        "exchanged": false,
        "modifications": {
          "elements": [
            {
              "id": "Y51BH3B9TGNA0",
              "lineItemRef": {
                "id": "0HZVYK48V91BA"
              },
              "name": "Lettuce",
              "amount": 50,
              "modifier": {
                "id": "VEBNWRNNVGRM4"
              }
            },
            {
              "id": "7AYCPT96PZKYG",
              "lineItemRef": {
                "id": "0HZVYK48V91BA"
              },
              "name": "American Cheese",
              "amount": 100,
              "modifier": {
                "id": "AKP74D4JAGN2E"
              }
            }
          ]
        },
        "refunded": false,
        "isRevenue": true,
        "taxRates": {
          "elements": [
            {
              "id": "68TVKPTQBXP3A",
              "lineItemRef": {
                "id": "0HZVYK48V91BA"
              },
              "name": "CT State Sales Tax",
              "rate": 635000,
              "isDefault": true
            }
          ]
        },
        "isOrderFee": false
      },
      {
        "id": "8E1W9QMYZ25DA",
        "orderRef": {
          "id": "J05ZAK6CSG90P"
        },
        "item": {
          "id": "K0HSC308635WT"
        },
        "name": "Diet Coke",
        "price": 495,
        "printed": false,
        "binName": "",
        "createdTime": 1713537026000,
        "orderClientCreatedTime": 1713472326000,
        "exchanged": false,
        "refunded": false,
        "isRevenue": true,
        "taxRates": {
          "elements": [
            {
              "id": "68TVKPTQBXP3A",
              "lineItemRef": {
                "id": "8E1W9QMYZ25DA"
              },
              "name": "CT State Sales Tax",
              "rate": 635000,
              "isDefault": true
            }
          ]
        },
        "isOrderFee": false
      },
      {
        "id": "R93PB1GCJGF3C",
        "item": {
          "id": "413349GGGK73J"
        },
        "createdTime": 1713538017502,
        "binName": "",
        "userData": null,
        "itemCode": "",
        "price": 275,
        "alternateName": "",
        "name": "Large Fries",
        "exchanged": false,
        "refunded": false,
        "printed": false,
        "isRevenue": true,
        "colorCode": null,
        "taxRates": {
          "elements": [
            {
              "id": "68TVKPTQBXP3A",
              "name": "CT State Sales Tax",
              "rate": 635000,
              "taxAmount": 0,
              "isDefault": true,
              "taxType": null
            }
          ]
        },
        "modifications": {
          "elements": [
            {
              "modifier": {
                "price": 0,
                "alternateName": null,
                "name": "Sauteed Onions",
                "id": "SJ86B81D8QT9W"
              },
              "name": "Sauteed Onions",
              "alternateName": null,
              "amount": 0,
              "id": "5WBG5VR4Y388P"
            }
          ]
        }
      }
    ]
  },
  "device": {
    "id": "5d48b9a2-ecc7-4aa2-bcb5-fdd5ffdc1cb4"
  }
}"""
}