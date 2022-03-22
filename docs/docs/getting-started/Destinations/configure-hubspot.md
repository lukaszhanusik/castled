---
sidebar_position: 6
---

# Hubspot

Hubspot is a leading cloud-based CRM, which provides a set of marketing and sales tools, which helps organizations better engage with their customers.

## Creating an app connection

Castled establishes connection to your hubspot destination via oauth. Select Hubspot as the source type and you will be redirected to the Hubspot login screen. Login via Hubspot to create an app connection in Castled. 


## Creating a sync pipeline

### App Sync Config

Sync config for Hubspot includes configuring the target object to sync the data and the sync mode.

#### Supported Objects

Castled supports the four standard objects **Contact**, **Company**, **Ticket** and **Deal** and any custom objects.

![hubspot object config](/img/screens/destinations/hubspot/hs_sync_object.png)


#### Sync Mode

Sync Mode defines how the incoming records will be synced in case if the record already exists on Hubspot. 

* **Insert**:  Create new records for each incoming record on Hubspot irrespective of whether the record is already present or not.

* **Update**: Updates existing records but ignores records which are not already present on Hubspot

* **Upsert**: Updates existing records and also create new entries for records which are not present on Hubspot


### Hubspot fields mapping

After configuring the sync settings, you will be taken to the mapping page, where you have to map the results of the source query to the target object fields. This includes both standard and custom fields. In case of Upsert and Update modes, you will also have to define how the records will be matched, which is required for Castled to uniquely identify a record

![hubspot mapping page](/img/screens/destinations/hubspot/hs_mapping.png)