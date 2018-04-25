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

This is the developers guide to Azure Client.

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

    1. **TenantID** The ID of the AAD (Azure Active Directory)  in which  application is created.
    2. **Application ID** The ID for  application  during  registrations in Azure Active Directory
    3. **Subscription Key** Subscription grants access to  Azure services and to the Azure Platform Management Portal
    4. **Resource Group**  Resource groups provide a way to monitor, control access, provision and manage billing for collections of assets that are required to run an application, or used by a client or company department
    5. **Acr Name** Same as ApplicationID
    6. **Storage Account** An Azure storage account provides a unique namespace to store and access Azure Storage data objects. All objects in a storage account are billed together as a group
    7. **Secret key**  Client Secret key for a web application registered with Azure Active Directory .


**2.1 Single Solution**

azure/singleImageAzureDeployment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

**2.2 Composite Solution**

azure/compositeSolutionAzureDeployment
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

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

