---
sidebar_position: 12
---

# Salesforce

Salesforce is a popular cloud based CRM, which helps the sales/marketing teams of your organization to better engage customers and leads.

## Creating an app connection

Castled establishes connection to your salesforce destination via oauth. Select Salesforce as the source type and you will be redirected to the Salesforce login screen. Login via salesforce to create an app connection in Castled. 


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
