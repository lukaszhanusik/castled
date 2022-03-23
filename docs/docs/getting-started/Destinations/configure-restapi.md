---
sidebar_position: 15
---

# Rest Api

With Castled's rest api connector, you can send customer/product data from your cloud data warehouse to any destination, which supports Rest Apis. Castled supports both single-record update and multi-record updates via bulk apis.

## Single Record Update

This section explains how to setup a pipeline if you want to sync data to a single record update api.

### Configure the sync settings

Parallelism config determines the parallelism with which Castled will hit your Rest api. A higher parallelims will fasten up the data sync but can add significant load on the destination server. A lower parallelism can slow down the data sync

Keep the **Enable bulk** checkbox unchecked.

![restapi sync config](/img/screens/destinations/restapi/rest_api_sync_config.png)

### Configure the api template(using mustache)

Configure the rest api url, headers and the body on the mapping page. The body needs to be a valid json and needs to be defined using the mustache template.  You need to map the incoming query fields to a rest api json template using mustache. Read more about mustache templating [here](https://mustache.github.io/mustache.5.html). Also refer the demo video below to see how to configure the mapping.

![restapi mapping](/img/screens/destinations/restapi/rest_api_mapping.png)

## Bulk Record Update

This section defines how you can configure your pipeline to sync the data to a Bulk Rest Api. Castled will batch together the incoming records from the source as per the configured batch size.

### Configure the sync settings

* Configure the parallelim field to control the parallelism in which Castled will hit the destination api. 
* Check **Enable bulk** checkbox. 
* Configure the json path of the json where the record mustache template is present using **Json Array Path** option. It should in the format parent.child.subchild.
* Configure the batch size. Batch size determines how the incoming records will be batched together. For instance, if the batch size is 100, Castled will batch 100 records together in one api call.

![restapi bulk sync config](/img/screens/destinations/restapi/rest_api_bulk_sync_config.png)

### Configure the api template(using mustache)

The rest api template for bulk api is very much similiar to the single record api. There are few differences though

* There should be a array of mustache jsons in the payload
* The path to the mustache json array should match the Json Array Path configured on the sync settings page.

Refere the demo video below for more information on how to configure the api template for bulk api.

![restapi bulk mapping](/img/screens/destinations/restapi/rest_api_bulk_mapping.png)


## Demo Video

<iframe width="560" height="315" src="https://www.youtube.com/embed/9fEF-tGCmlQ" title="YouTube video player" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" allowfullscreen></iframe>
