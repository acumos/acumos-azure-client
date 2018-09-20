.. ===============LICENSE_START=======================================================
.. Acumos CC-BY-4.0
.. ===================================================================================
.. Copyright (C) 2017-2018 AT&T Intellectual Property & Tech Mahindra. All rights reserved.
.. ===================================================================================
.. This Acumos documentation file is distributed by AT&T and Tech Mahindra
.. under the Creative Commons Attribution 4.0 International License (the "License");
.. you may not use this file except in compliance with the License.
.. You may obtain a copy of the License at
..
.. http://creativecommons.org/licenses/by/4.0
..
.. This file is distributed on an "AS IS" BASIS,
.. WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
.. See the License for the specific language governing permissions and
.. limitations under the License.
.. ===============LICENSE_END=========================================================

====================================
Acumos Azure Client Developers Guide
====================================

1. Introduction
---------------
Microsoft Azure is a cloud computing service created by Microsoft for building, testing, deploying, and managing applications and services through a global network of Microsoft-managed data centers.


**1.1 What is Azure Client?**

Acumos provides deployment of model in Azure cloud :

   1. Deploy single solution from Acumos marketplace in Azure clould.

   2. Deploy composite solution from Acumos marketplace in Azure clould. 

   
**1.2 Target Users**

   This guide is targeted towards the open source user community that:

   1. Intends to understand the functionality of the Acumos Azure client.

**1.3 Acumos Azure client - Flow Chart**

         .. image:: images/azure_client_flowchart.jpg
            :alt: Azure client flow chart



**1.5 Acumos Azure client Flow Structure:**

   

    **Page Name:** Model/Solution Landing Page

      

      1.  Clicking on <Deploy to Cloud> for Deploy model .

      2.  <Deploy to Cloud>  should prompt details about MS Azure (Inputs
          TBD),Rackspace etc..
	   
      3. Select <Microsoft Azure> from Drop down and fill all details for Deployment.
      	  


  

2. Model Deployment
-------------------

.. image:: images/AzureDetails.jpg

#. **Application ID** The ID for  application  during  registrations in Azure Active Directory
#. **TenantID** The ID of the AAD (Azure Active Directory)  in which  application is created
#. **Secret key**  Client Secret key for a web application registered with Azure Active Directory
#. **Subscription Key** Subscription grants access to  Azure services and to the Azure Platform Management Portal
#. **Resource Group**  Resource groups provide a way to monitor, control access, provision and manage billing for collections of assets that are required to run an application, or used by a client or company department
#. **Acr Name** Same as ApplicationID
#. **Storage Account** An Azure storage account provides a unique namespace to store and access Azure Storage data objects. All objects in a storage account are billed together as a group


Steps
^^^^^

#. Acumos Marketplace: Users can go marketplace and discover models by browsing, direct search, or by applying any of a number of filter criteria to explore the marketplace. Models are presented on the Marketplace as "tiles", showing the Name, image, ratings and usage statistics.
#.  Azure client: User can deploy model in azure cloud if Deploy to cloud button is enable in model detail page. User   select Microsoft azure to deploy model in azure cloud. Acumos have two type of model to deploy azure cloud.
#. User can fill azure authentication detail and deploy simple solution in azure cloud.
#. Composite model is combination of more than one solutions. Model connector also deploy with composite models. Model connector is use for communication  between models in virtual machine.
#. User can set databroker details with composite mode. Databroker image is available in                  composite solution then it will also deploy with composite solution.
#. Azure client send notification to user after deploying composite solution. Notification have      endpoint of model connector and databroker.
#. Composite solution endpoint’s also save in database. User can check with UUID number.


API
---

**2.1 Azure Single Image Deployment**

**- Trigger**

This API is used to deploy single solution in Azure cloud.
Check with your Cloud Administrator for values to populate the request.

**- Request**

    {
    "acrName": "<acr name>",
    
    "client": "<client ID>",
    
    "imagetag": "<image tag>",
    
    "key": "<key>",
    
    "rgName": "<rg name>",
    
    "solutionId": "<your model solution id>",
    
    "solutionRevisionId": "<your model solution revision id>",
    
    "storageAccount": "<storage account name>",
    
    "subscriptionKey": "<your subscription key>",
    
    "tenant": "<your tenant ID>",
    
    "userId": "<your user ID>"
    
    }

**- Response**

{
"status": "SUCCESS",
"UIDNumber": "Unique Transaction Number"
}

**2.2 Azure Composite Solution**


**- Trigger:**

    This API is used to deploy Composite solution in Azure cloud.
    Check with your Cloud Administrator for values to populate the request.

**- Request:**

  {
  "acrName": "<acr name>",
  
  "client": "<client ID>",
  
  "jsonMapping": "testMapping",
  
  "jsonPosition": "testPosition",
  
  "key": "<key>",
  
  "rgName": "<rg name>",
  
  "solutionId": "<your model solution id>",

  "solutionRevisionId": "<your model solution revision id>",

 "storageAccount": "<storage account name>",
  
  "subscriptionKey": "<your subscription key>",
  
  "tenant": "<your tenant ID>",
  
  "urlAttribute": "testUrl",
  
  "userId": "<your user ID>"
  }

**- Response:**

{
"status": "SUCCESS",
"UIDNumber": "Unique Transaction Number"
}

