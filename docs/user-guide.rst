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

=========================================
Acumos Azure Client Users Guide
=========================================


1. Introduction
======================
This is a user’s guide that describes how to use the the Azure client.

	**1.1    What is the Azure ?**
	
		Microsoft Azure is a cloud computing service created by Microsoft for building, testing, deploying, and managing applications and services through a global network of Microsoft-managed data centers.
		
		   1. Easy Deploy Simple models.  The Azure client deploy simple models easily in azure cloud. It created new virtual machine and deployed models in 
	          virtual machine.
			  
		   2. Easy Deploy Composite models. The Azure client deploy Composite models easily in azure cloud. It created new virtual machine and deployed models in   
		      virtual machine .it provided endpoint of models and endpoint URL is showing as notification after deployment.
			  
	**1.2    Target Users**
	
		 The Azure-client is designed for users who wish to deploy Models in azure cloud in separate virtual machine.

2. Creating a Login on Acuomos
    
	In order to use the full capabilities of Acuomos, the users must create a login on the Acumos Portal.
	
==================================================
3. Azure-client Experience - for Users
==================================================

	3.1 Acumos Marketplace**
	
	Users can go marketplace and discover models by browsing, direct search, or by applying any of a number of filter criteria to explore the marketplace. Models are presented on the Marketplace as "tiles", showing the Name, image, ratings and usage statistics. 
	
	3.2 Azure client**
	
	  User can deploy model in azure cloud if Deploy to cloud button is enable in model detail page. User   select Microsoft azure to deploy model in azure cloud. Acumos have two type of model to deploy azure cloud.
	  
			1. User can fill azure authentication detail and deploy simple solution in azure cloud.
			
						  .. Image:: images/ModelDetails.jpg
							  : alt : Model Detail
	
		   2. Composite model is combination of more than one solutions. Model connector also deploy with    composite  models. Model connector is use for 
			 communication  between models in virtual machine.
	
						   .. Image:: images/AzureDetails.jpg
							  : alt: Azure Details
	
	
		  3. User can set databroker details with composite mode. Databroker image is available in                  composite solution then it will also deploy with composite solution.
						   .. Image :: images/AzureDetails.jpg
							  : alt: Azure Details with databroker
	
	      4. Azure client send notification to user after deploying composite solution. Notification have      endpoint of model connector and databroker.
	
	      5. Composite solution endpoint’s also save in database. User can check with UUID number.     
	





