---
sidebar_position: 14
---

# Google Pub/Sub

Pub/Sub is used for streaming analytics and data integration pipelines to ingest and distribute data.

## Creating an app connection

For configuring a new app connector for Google Pub/Sub, the following fields needs to be captured
- **Destination App Name**
    - A name you want to call this connector
- **Project ID**
    - A project ID is a unique string used to differentiate your project from all others in Google Cloud.You can use the Cloud Console to [generate Project ID](https://cloud.google.com/resource-manager/docs/creating-managing-projects).
- **Service Account JSON File**
    - Create a Service Account having the required roles for publishing data to a topic. The public keys for the created Service Account can be downloaded as JSON file and the same JSON file needs to be uploaded here. Use [Create Service Account](https://cloud.google.com/iam/docs/creating-managing-service-account-keys) to create the service account.

![Docusaurus](/img/screens/destinations/google-pubsub/app_pubsub_app_config.png)


## Creating a sync pipeline

- **Select the Pub/Sub Topics to sync**
  - Select the pub/sub topic to which you need to sync the data
- **Select the Sync mode**
  - Select the sync mode for moving data and it is always INSERT for google pub/sub topics.
![Docusaurus](/img/screens/destinations/google-pubsub/app_pubsub_app_sync_settings.png)

- **Field Mapping**
  -  `Which column would you like to sync as custom destination fields`
  Use this section to sync any warehouse column as a custom destination field in the destination object.The properties configured here will be added as new custom properties. By default for google pub/sub all the warehouse columns are automapped.You can add,remove or modify the mapping fields as per the requirement.
![Docusaurus](/img/screens/destinations/google-pubsub/app_pubsub_field_mapping.png)

