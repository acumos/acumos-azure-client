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

==========================================
Acumos Azure Client Developers Guide
==========================================

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
-------------------------------

**2.1 Single Solution**

 - azure/singleImageAzureDeployment
~~~~~~~~~~~~~~~

**- Trigger**

This API is used to deploy single solution in Azure cloud.

**- Request**

{
"acrName": "CognitaE6Reg",

"client": "c83923c9-73c4-43e2-a47d-2ab700ac9353",

"imagetag": "cognita-nexus01:8001/newadder1:1",

"key": "eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO/dpvw6CXwpc=",

"rgName": "Cognita-OAM",

"solutionId": "02eab846-2bd0-4cfe-8470-9fc69fa0d877",

"solutionRevisionId": "a9e68bc6-f4b4-41c6-ae8e-4e97ec3916a6",

"storageAccount": "cognitae6storage",

"subscriptionKey": "81f6511d-7cc6-48f1-a0d1-d30f65fdbe1a",

"tenant": "412141bb-9e53-4aed-8468-6868c832e618",

"userId": "0505e537-ce79-4b1f-bf43-68d88933c369"

}

**- Response**

{
"status": "SUCCESS",
"UIDNumber": "Unique Transaction Number"
}

**2.2 Composite Solution**

- azure/compositeSolutionAzureDeployment
~~~~~~~~~~~~~~~~~~~~

**- Trigger:**

    This API is used to deploy Composite solution in Azure cloud.

**- Request:**

  {
  "acrName": "CognitaE6Reg",
  
  "client": "c83923c9-73c4-43e2-a47d-2ab700ac9353",
  
  "jsonMapping": "testMapping",
  
  "jsonPosition": "testPosition",
  
  "key": "eN0TksgjTtrzeRHR5vQmvdIFEjkPjuHO/dpvw6CXwpc=",
  
  "rgName": "Cognita-OAM",
  
  "solutionId": "b318f607-90a6-4a14-af4c-de6dad2244d0",
  
  "solutionRevisionId": "1be01a3f-830b-413e-a280-fdd97246c8ab",
  
  "storageAccount": "cognitae6storage",
  
  "subscriptionKey": "81f6511d-7cc6-48f1-a0d1-d30f65fdbe1a",
  
  "tenant": "412141bb-9e53-4aed-8468-6868c832e618",
  
  "urlAttribute": "testUrl",
  
  "userId": "7cd47ca4-1c5d-4cdc-909c-f7c17367b4d4"
  }

**- Response:**

{
"status": "SUCCESS",
"UIDNumber": "Unique Transaction Number"
}

