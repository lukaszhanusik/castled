---
sidebar_position: 14
---

# Mixpanel

Mixpanel supports API endpoints that can query, export, and import data as well as manipulate Mixpanel metadata.Mixpanel's Ingestion APIs allow you to send event or profile data to Mixpanel.

## Creating an app connection

For configuring a new app connector for mixpanel, the following fields needs to be captured
- **Name**
    - A name you want to call this connector
- **Project Token**
    - Project token is provided as a value inside of the data sent to mixpanel
- **API Secret**
    - Project API Secret is required for ingesting any events with a timestamp of more than five days

![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_app_config.png)

**Login** to Mixpanel and navigate (https://mixpanel.com/settings/project/) to **Project Settings > Overview > Access Keys**  to get **Project Token ** and **API Secret** required for configuring the app connector

![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_settings.png)


## Creating a sync pipeline

### Objects to sync
Castled supports syncing the following Iterable objects:

1. **Events**
2. **User Profile**
3. **Group Profile**
![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_sync_objects.png)

### Event Object
For Event Object, the following fields needs to be captured
- **Event Name**
    - Mixpanel recommends using the name of the table as the name of the event
- **Sync Mode**
    - Castled supports only **INSERT** mode for Event Object

![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_event_object.png)
- **Field Mapping**
The following information needs to be captured as part of field mapping for events
  -  `Column identifying the event being synced`
    This field will be used at the destination to uniquely identify the event being synced
  -  `Column identifying the user associated with the event` 
  This field will be used to uniquely identify the user associated with the event
  -  `Column identifying Event Timestamp`
  This field will be used to capture the timestamp of the event if available.
![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_event_field_mapping.png)
  -  `Which column would you like to sync as custom destination fields`
  Use this section to sync any other object property as a custom destination field in the destination object.The properties configured here will get added as new custom properties.

### User Profile Object
For User Profile Object, the following fields needs to be captured
- **Sync Mode**
    - Castled supports only **UPSERT** mode for User Profile Object

![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_user_profile.png)
- **Field Mapping**
The following information needs to be captured as part of field mapping for User Profile
  -  `User ID`
    This field will be used at the destination to uniquely identify the User Profile being synced
  -  `Which column would you like to sync as custom destination fields`
  Use this section to sync any other object property as a custom destination field in the destination object.The properties configured here will get added as new custom properties.
![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_user_profile_field_mapping.png)

### Group Profile Object
For Group Profile Object, the following fields needs to be captured
- **Group Key**
    - Mixpanel project specific group key
- **Sync Mode**
    - Castled supports only **UPSERT** mode for Group Profile Object

![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_group_profile.png)

- **Field Mapping**
The following information needs to be captured as part of field mapping for Group Profile
  -  `Group ID`
    This field will be used at the destination to uniquely identify the Group Profile being synced
  -  `Which column would you like to sync as custom destination fields`
  Use this section to sync any other object property as a custom destination field in the destination object.The properties configured here will get added as new custom properties.
![Docusaurus](/img/screens/destinations/mixpanel/app_mixpanel_grouo_profile_field_mapping.png)
