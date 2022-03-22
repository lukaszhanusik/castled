---
sidebar_position: 9
---

# Intercom

Intercom is primarly a customer communications platform, which can be used to provide live support to your customers.

## Creating an app connection

Castled requires the access token of your intercom workspace to establish a connection to intercom. Find details on how to find your intercom access token [here](https://developers.intercom.com/building-apps/docs/authentication-types#section-access-tokens)

## Creating a sync pipeline

### Sync Config

Select target intercom object to sync the data. Castled supports syncing the following objects

* **Lead** : The contact details from the data warehouse will be synced as a Lead in Intercom.

* **User** : The contact details from the data warehouse will be synced as a User in Intercom.

* **Company** : Company information of the lead or user

#### Sync Mode

Sync Mode defines how the incoming records will be synced in case if the record already exists on Intercom. 

* **Update**: Updates existing records but ignores records which are not already present on Salesforce

* **Upsert**: Updates existing records and also create new entries for records which are not present on Salesforce

#### Associate contacts to company

If you want to associate a new user or lead with a company, enable the checkbox to associate contacts to company

![intercom sync config](/img/screens/destinations/intercom/intercom_sync_config.png)

### Destination Fields Mapping

In case of Lead or User, map email or any other unique user identifier(external_id) as the record identifier.
In case of Company, map a unique company identifier to company_id.

Also map other query fields to corresponding intercom object fields. 


![intercom mapping](/img/screens/destinations/intercom/intercom_mapping.png)

