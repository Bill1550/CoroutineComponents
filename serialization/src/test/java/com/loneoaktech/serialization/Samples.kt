package com.loneoaktech.serialization

object Samples {

  val oneItemOrder = """
{
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

  val threeItemOrder = """
{
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


  val bigSample="""
{
  "href":"https:\/\/dev1.dev.clover.com\/v3\/merchants\/4G5KV3R6WKGD5\/orders\/J05ZAK6CSG90P",
  "id":"J05ZAK6CSG90P",
  "currency":"USD",
  "employee": {
    "href":"https:\/\/dev1.dev.clover.com\/v3\/merchants\/4G5KV3R6WKGD5\/employees\/0KFCCZY3NJAFY",
    "id":"0KFCCZY3NJAFY",
    "name":"Bill",
    "nickname":"Hart",
    "customId":"Boss",
    "role":"ADMIN",
    "orders": {
      "href":"https:\/\/dev1.dev.clover.com\/v3\/merchants\/4G5KV3R6WKGD5\/employees\/0KFCCZY3NJAFY\/orders"
    }
  },
  "total":14187,
  "paymentState":"OPEN",
  "title":"101 - Main Dining Room",
  "orderType": {
    "id":"WC1FGSYABMJKM",
    "labelKey":"com.clover.order.type.dine_in",
    "label":"Dine-in",
    "taxable":true,
    "isDefault":true,
    "filterCategories":false,
    "isHidden":false,
    "fee":0,
    "minOrderAmount":0,
    "maxOrderAmount":0,
    "maxRadius":0,
    "avgOrderTime":0,
    "hoursAvailable":"BUSINESS",
    "isDeleted":false,
    "systemOrderTypeId":"DINE-IN-TYPE"
  },
  "taxRemoved":false,
  "isVat":false,
  "state":"open",
  "manualTransaction":false,
  "groupLineItems":true,
  "testMode":false,
  "createdTime":1713472326000,
  "clientCreatedTime":1713472326000,
  "modifiedTime":1713537285000,
  "lineItems": {
    "elements": [
      {
        "id":"0HZVYK48V91BA",
        "orderRef": {"id":"J05ZAK6CSG90P"},
        "item": {"id":"55TM5VN2HNQM4"},
        "name":"Hamburger",
        "price":995,
        "printed":false,
        "binName":"",
        "createdTime":1713472344000,
        "orderClientCreatedTime":1713472326000,
        "exchanged":false,
        "modifications": {
          "elements": [
            {"id":"Y51BH3B9TGNA0","lineItemRef":{"id":"0HZVYK48V91BA"},"name":"Lettuce","amount":50,"modifier":{"id":"VEBNWRNNVGRM4"}},
            {"id":"7AYCPT96PZKYG","lineItemRef":{"id":"0HZVYK48V91BA"},"name":"American Cheese","amount":100,"modifier":{"id":"AKP74D4JAGN2E"}}
          ]
        },
        "refunded":false,
        "isRevenue":true,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","lineItemRef":{"id":"0HZVYK48V91BA"},"name":"CT State Sales Tax","rate":635000,"isDefault":true}
          ]
        },
        "isOrderFee":false
      },
      {
        "id":"8E1W9QMYZ25DA",
        "orderRef": {"id":"J05ZAK6CSG90P"},
        "item":{"id":"K0HSC308635WT"},
        "name":"Diet Coke",
        "price":495,
        "printed":false,
        "binName":"",
        "createdTime":1713537026000,
        "orderClientCreatedTime":1713472326000,
        "exchanged":false,
        "refunded":false,
        "isRevenue":true,
        "taxRates": {
          "elements":[
            {"id":"68TVKPTQBXP3A","lineItemRef":{"id":"8E1W9QMYZ25DA"},"name":"CT State Sales Tax","rate":635000,"isDefault":true}
          ]
        },
        "isOrderFee":false
      },
      {
        "id":"R93PB1GCJGF3C",
        "item":{"id":"413349GGGK73J"},
        "createdTime":1713538017502,
        "binName":"",
        "userData":null,
        "itemCode":"",
        "price":275,
        "alternateName":"",
        "name":"Large Fries",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier":{"price":0,"alternateName":null,"name":"Sauteed Onions","id":"SJ86B81D8QT9W"},"name":"Sauteed Onions","alternateName":null,"amount":0,"id":"5WBG5VR4Y388P"}
          ]
        }
      },
      {
        "id":"VNFWARBCN9GPM",
        "item": {"id":"20DJEN9XVVZHA"},
        "createdTime":1713543044657,
        "binName":"",
        "userData":null,
        "itemCode":null,
        "price":1460,
        "alternateName":null,
        "name":"Fish Sandwich",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier":{"price":50,"alternateName":null,"name":"Lettuce","id":"VEBNWRNNVGRM4"},"name":"Lettuce","alternateName":null,"amount":50,"id":"JW8X8MY86F7SA"},
            {"modifier":{"price":100,"alternateName":null,"name":"American Cheese","id":"AKP74D4JAGN2E"},"name":"American Cheese","alternateName":null,"amount":100,"id":"AZ8Z1TD1H7GZG"},
            {"modifier":{"price":100,"alternateName":null,"name":"Bacon","id":"XKTG5KQFG9WKY"},"name":"Bacon","alternateName":null,"amount":100,"id":"M4QNSAFYYKZQE"}
          ]
        }
      },
      {
        "id":"19E41YWJ8FAJ4",
        "item": {"id":"20DJEN9XVVZHA"},
        "createdTime":1713543053381,
        "binName":"",
        "userData":null,
        "itemCode":null,
        "price":1460,
        "alternateName":null,
        "name":"Fish Sandwich",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier": {"price":50, "alternateName":null,"name":"Lettuce","id":"VEBNWRNNVGRM4"},"name":"Lettuce","alternateName":null,"amount":50,"id":"HRGN2E39M7VVR"},
            {"modifier":{"price":100,"alternateName":null,"name":"American Cheese","id":"AKP74D4JAGN2E"},"name":"American Cheese","alternateName":null,"amount":100,"id":"H7WWB087NZFRY"},
            {"modifier":{"price":100,"alternateName":null,"name":"Bacon","id":"XKTG5KQFG9WKY"},"name":"Bacon","alternateName":null,"amount":100,"id":"V4W6Y5HZQXN34"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"RMPXTW2H5N6HW",
        "item":{"id":"20DJEN9XVVZHA"},
        "createdTime":1713543053923,
        "binName":"",
        "userData":null,
        "itemCode":null,
        "price":1460,
        "alternateName":null,
        "name":"Fish Sandwich",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": { 
          "elements": [
            {"modifier":{"price":50,"alternateName":null,"name":"Lettuce","id":"VEBNWRNNVGRM4"},"name":"Lettuce","alternateName":null,"amount":50,"id":"YE61XAR0J0MH0"},
            {"modifier":{"price":100,"alternateName":null,"name":"American Cheese","id":"AKP74D4JAGN2E"},"name":"American Cheese","alternateName":null,"amount":100,"id":"3D62JNWMM970T"},
            {"modifier":{"price":100,"alternateName":null,"name":"Bacon","id":"XKTG5KQFG9WKY"},"name":"Bacon","alternateName":null,"amount":100,"id":"NKJS71Q61GD06"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"F8K89V6SJXBZM",
        "item":{"id":"20DJEN9XVVZHA"},
        "createdTime":1713543054416,
        "binName":"",
        "userData":null,
        "itemCode":null,
        "price":1460,
        "alternateName":null,
        "name":"Fish Sandwich",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier":{"price":50,"alternateName":null,"name":"Lettuce","id":"VEBNWRNNVGRM4"},"name":"Lettuce","alternateName":null,"amount":50,"id":"T85VWPDMMY170"},
            {"modifier":{"price":100,"alternateName":null,"name":"American Cheese","id":"AKP74D4JAGN2E"},"name":"American Cheese","alternateName":null,"amount":100,"id":"48X0MGPRX5TZA"},
            {"modifier":{"price":100,"alternateName":null,"name":"Bacon","id":"XKTG5KQFG9WKY"},"name":"Bacon","alternateName":null,"amount":100,"id":"HWANH2V1928VR"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"JM49RG8QG8YTT",
        "item": {"id":"20DJEN9XVVZHA"},
        "createdTime":1713543056512,
        "binName":"","userData":null,
        "itemCode":null,"price":1460,
        "alternateName":null,
        "name":"Fish Sandwich",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements":[
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier":{"price":50,"alternateName":null,"name":"Lettuce","id":"VEBNWRNNVGRM4"},"name":"Lettuce","alternateName":null,"amount":50,"id":"KN70DYCF3CQHC"},
            {"modifier":{"price":100,"alternateName":null,"name":"American Cheese","id":"AKP74D4JAGN2E"},"name":"American Cheese","alternateName":null,"amount":100,"id":"1312ARFC7ESCT"},
            {"modifier":{"price":100,"alternateName":null,"name":"Bacon","id":"XKTG5KQFG9WKY"},"name":"Bacon","alternateName":null,"amount":100,"id":"7Z83FBF24W4Q4"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"EWQA8Q0MYGS7E",
        "item": {"id":"92TB2HPY8MA7G"},
        "createdTime":1713543074664,
        "binName":"",
        "userData":null,
        "itemCode":"",
        "price":525,
        "alternateName":"",
        "name":"Jumbo Fries",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates": {
          "elements": [
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications": {
          "elements": [
            {"modifier":{"price":0,"alternateName":null,"name":"Mustard","id":"ZQE07497FZQTE"},"name":"Mustard","alternateName":null,"amount":0,"id":"F1VBPW3ZT1RMR"},
            {"modifier":{"price":50,"alternateName":null,"name":"Horse Raddish","id":"ZAP31WDJ1QBFP"},"name":"Horse Raddish","alternateName":null,"amount":50,"id":"HX019Z7M420BR"},
            {"modifier":{"price":0,"alternateName":null,"name":"Mayonnaise","id":"GQNFKK3Q3F4P4"},"name":"Mayonnaise","alternateName":null,"amount":0,"id":"FP7WRVX7ES496"},
            {"modifier":{"price":0,"alternateName":null,"name":"Ketchup","id":"Z3EMQ6KGEZ0BP"},"name":"Ketchup","alternateName":null,"amount":0,"id":"KJ0G6JPTJE49G"}
          ]
        }
      },
      {
        "id":"HQ57YZWASWEBJ",
        "item": {"id":"92TB2HPY8MA7G"},
        "createdTime":1713543091372,
        "binName":"",
        "userData":null,
        "itemCode":"",
        "price":525,
        "alternateName":"",
        "name":"Jumbo Fries",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates":{
          "elements":[
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications":{
          "elements":[
            {"modifier":{"price":0,"alternateName":null,"name":"Mustard","id":"ZQE07497FZQTE"},"name":"Mustard","alternateName":null,"amount":0,"id":"BS83093B3ZW1G"},
            {"modifier":{"price":50,"alternateName":null,"name":"Horse Raddish","id":"ZAP31WDJ1QBFP"},"name":"Horse Raddish","alternateName":null,"amount":50,"id":"1CJ24ETXTG8QM"},
            {"modifier":{"price":0,"alternateName":null,"name":"Mayonnaise","id":"GQNFKK3Q3F4P4"},"name":"Mayonnaise","alternateName":null,"amount":0,"id":"5S8PZZ1H1WNPT"},
            {"modifier":{"price":0,"alternateName":null,"name":"Ketchup","id":"Z3EMQ6KGEZ0BP"},"name":"Ketchup","alternateName":null,"amount":0,"id":"HJZ4BSFXE9XTY"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"H78HMFBVZT6SW",
        "item":{"id":"92TB2HPY8MA7G"},
        "createdTime":1713543091983,
        "binName":"",
        "userData":null,
        "itemCode":"",
        "price":525,
        "alternateName":"",
        "name":"Jumbo Fries",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates":{
          "elements":[
            {"id":"68TVKPded":false,"printed":false,"isRevenue":true,"colorCode":null,"taxRates":{"elements":[{"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications":{
          "elements":[
            {"modifier":{"price":0,"alternateName":null,"name":"Mustard","id":"ZQE07497FZQTE"},"name":"Mustard","alternateName":null,"amount":0,"id":"NBF4G3V7K4B4Y"},
            {"modifier":{"price":50,"alternateName":null,"name":"Horse Raddish","id":"ZAP31WDJ1QBFP"},"name":"Horse Raddish","alternateName":null,"amount":50,"id":"R51YY49XTK6JR"},
            {"modifier":{"price":0,"alternateName":null,"name":"Mayonnaise","id":"GQNFKK3Q3F4P4"},"name":"Mayonnaise","alternateName":null,"amount":0,"id":"ST8FVPR8GMCV4"},
            {"modifier":{"price":0,"alternateName":null,"name":"Ketchup","id":"Z3EMQ6KGEZ0BP"},"name":"Ketchup","alternateName":null,"amount":0,"id":"DAQ02STTW2JF8"}
          ]
        },
        "discounts":null,
        "payments":null
      },
      {
        "id":"NXPV86K3W56G8",
        "item":{"id":"92TB2HPY8MA7G"},
        "createdTime":1713543094296,
        "binName":"",
        "userData":null,
        "itemCode":"",
        "price":525,
        "alternateName":""
        ,"name":"Jumbo Fries",
        "exchanged":false,
        "refunded":false,
        "printed":false,
        "isRevenue":true,
        "colorCode":null,
        "taxRates":{
          "elements":[
            {"id":"68TVKPTQBXP3A","name":"CT State Sales Tax","rate":635000,"taxAmount":0,"isDefault":true,"taxType":null}
          ]
        },
        "modifications":{
          "elements":[
            {"modifier":{"price":0,"alternateName":null,"name":"Mustard","id":"ZQE07497FZQTE"},"name":"Mustard","alternateName":null,"amount":0,"id":"4DV9P2W0SCMJY"},
            {"modifier":{"price":50,"alternateName":null,"name":"Horse Raddish","id":"ZAP31WDJ1QBFP"},"name":"Horse Raddish","alternateName":null,"amount":50,"id":"SY9HV6DKCMX6T"},
            {"modifier":{"price":0,"alternateName":null,"name":"Mayonnaise","id":"GQNFKK3Q3F4P4"},"name":"Mayonnaise","alternateName":null,"amount":0,"id":"FRCZDM81FEW9A"},
            {"modifier":{"price":0,"alternateName":null,"name":"Ketchup","id":"Z3EMQ6KGEZ0BP"},"name":"Ketchup","alternateName":null,"amount":0,"id":"JHTQ431G9429C"}
          ]
        },
        "discounts":null,
        "payments":null
      }
    ]
  },
  "device":{"id":"5d48b9a2-ecc7-4aa2-bcb5-fdd5ffdc1cb4"}
}     
"""
}