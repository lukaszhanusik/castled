{*
   ------ KAFKA ------
*}

{{/*
Set kafka fullname
*/}}
{{- define "castled.kafka.fullname" -}}
{{- if .Values.kafka.fullnameOverride -}}
{{- .Values.kafka.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else if .Values.kafka.nameOverride -}}
{{- printf "%s-%s" .Release.Name .Values.kafka.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" (include "castled.fullname" .) "kafka" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Set kafka host
*/}}
{{- define "castled.kafka.host" -}}
{{- if .Values.kafka.enabled -}}
    {{- template "castled.kafka.fullname" . -}}
{{- else -}}
{{- .Values.externalKafka.host -}}
{{- end -}}
{{- end -}}

{{/*
Set kafka port
*/}}
{{- define "castled.kafka.port" -}}
{{- if .Values.kafka.enabled -}}
    {{- 9092 -}} 
{{- else -}}
   {{- .Values.externalKafka.port -}}
{{- end -}}
{{- end -}}

{{/*
Set kafka url
*/}}
{{- define "castled.kafka.url" -}}
{{- if .Values.kafka.url -}}
    {{- .Values.kafka.url | quote -}}
{{- else -}}
    "kafka://{{- template "castled.kafka.host" . -}}:{{-  template "castled.kafka.port" . -}}"
{{- end -}}
{{- end -}}

{{/*
Set kafka no protocol url 
*/}}
{{- define "castled.kafka.url_no_protocol" -}}
{{- if .Values.kafka.url -}}
    {{- .Values.kafka.url | quote -}}
{{- else -}}
    {{- template "castled.kafka.host" . -}}:{{-  template "castled.kafka.port" . -}}
{{- end -}}
{{- end -}}