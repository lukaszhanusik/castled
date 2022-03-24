---
sidebar_position: 16
---

# Salesforce

Salesforce is a popular cloud based CRM, which helps the sales/marketing teams of your organization to better engage customers and leads.

## Creating an app connection

Castled establishes connection to your salesforce destination via OAuth. Select Salesforce as the source type, enter the OAuth ClientID and ClientSecret of the connected app. You will be redirected to the Salesforce login screen. Login via salesforce to create an app connection in Castled. 

Follow the steps [here](https://docs.datawatch.com/swarm/desktop/Generating_a_Client_ID_and_ClientSecret_Key_for_Salesforce_Connections.htm) to create a new connected app in salesforce and accesss the oauth ClientID and Client Secret.


## Creating a sync pipeline

### Sync Config

Sync config for Salesforce includes configuring the target object to sync the data and the sync mode.

#### Target Object

Select the target object to sync the data from a dropdown of configured objects. This includes both the standard and custom objects.

![salesforce object config](/img/screens/destinations/salesforce/sf_sync_object.png)


#### Sync Mode

Sync Mode defines how the incoming records will be synced in case if the record already exists on Salesforce. 

* **Insert**:  Create new records for each incoming record on Salesforce irrespective of whether the record is already present or not.

* **Update**: Updates existing records but ignores records which are not already present on Salesforce

* **Upsert**: Updates existing records and also create new entries for records which are not present on Salesforce

### Salesforce fields mapping

After configuring the sync settings, you will be taken to the mapping page, where you have to map the results of the source query to the target object fields. This includes both standard and custom fields. In case of Upsert and Update modes, you will also have to define how the records will be matched, which is required for Castled to uniquely identify a record

![salesforce mapping page](/img/screens/destinations/salesforce/sf_mapping.png)

## Demo Video

<iframe width="560" height="315" src="https://www.youtube.com/embed/Qro2tcJZoa4" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
