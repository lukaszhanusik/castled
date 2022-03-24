---
sidebar_position: 10
---

# Iterable

Iterable is a cross-channel marketing platform that powers unified customer experiences and empowers you to create, optimize and measure every interaction across the entire customer journey. Iterable lets you create a seamless, individualized experience for your customers across email, SMS, mobile push, web push, in-app notifications, social and more.

## Creating an app connection

![iterable app config form](/img/screens/destinations/iterable/app_config.png)

Follow the steps to get the **Api Key** for creating a connection.

1. Login to your Iterable account.
2. Select `Integrations` on navbar and then click on `API keys`. In the Api Keys page click on `New API key`.

   ![Iterable api key page](/img/screens/destinations/iterable/iterable_api_key_page.png)

3. In the create api key popup, provide a name for the key. Also for the type choose `Server-side`. This will let Castled access the api end points for syncing users, events and catalogs.

   ![Iterable api key page](/img/screens/destinations/iterable/iterable_api_key_type.png)

## Creating a sync pipeline

### Sync settings

![iterable sync config form](/img/screens/destinations/iterable/sync_config_1.png)

Castled supports syncing the following Iterable objects:

1. **Users**
2. **Events**

   ![iterable sync config event](/img/screens/destinations/iterable/sync_config_events.png)

   **Template Id** and **Campaign Id** are optional fields. You can also map these fields in the mapping screen, in that case the value provided in this step will be overrided.

3. **Catalogs**

### Field Mapping

In this step we define how each field in the destination object gets updated.

1.  **Users**

    `Email` is the primary key here. In case an user with the given email already exists, user fields will be updated in Iterable.

    ![iterable mapping users](/img/screens/destinations/iterable/mapping_users.png)

    Use the above section of the mapping page, if you want to create any custom field in the user object.

2.  **Events**

    `eventName` is a mandatory field. If an event with the same `id` exists in Iterable, it's fields will be updated based on incomming records, else a new event record is created.

    ![iterable mapping events](/img/screens/destinations/iterable/mapping_events.png)

    You **must** select at least one of the `email` or `userId` fields in the above section of mapping page, or else iterable discards such event records.

3.  **Catalogs**

    `itemId` is the primary key here. You can create a new field for your catalog item using the new field section of the mapping page. Castled will create this field for your catalog item when it syncs the item to Iterable
