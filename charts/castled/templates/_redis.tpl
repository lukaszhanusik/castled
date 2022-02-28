{*
   ------ REDIS ------
*}

{{/*
Return the Redis fullname
*/}}
{{- define "castled.redis.fullname" -}}
{{- if .Values.redis.fullnameOverride -}}
{{- .Values.redis.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else if .Values.redis.nameOverride -}}
{{- printf "%s-%s" .Release.Name .Values.redis.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" (include "castled.fullname" .) "redis" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Return the Redis host
*/}}
{{- define "castled.redis.host" -}}
{{- if .Values.redis.enabled }}
    {{- printf "%s-master" (include "castled.redis.fullname" .) -}}
{{- else -}}
    {{- printf "%s" .Values.externalRedis.host -}}
{{- end -}}
{{- end -}}

{{/*
Return the Redis port
*/}}
{{- define "castled.redis.port" -}}
{{- if .Values.redis.enabled }}
    {{- printf "6379" | quote -}}
{{- else -}}
    {{- .Values.externalRedis.port | quote -}}
{{- end -}}
{{- end -}}

{{/*
Return true if a secret object for Redis should be created
*/}}
{{- define "castled.redis.createSecret" -}}
{{- if and (not .Values.redis.enabled) (not .Values.externalRedis.existingSecret) .Values.externalRedis.password }}
    {{- true -}}
{{- end -}}
{{- end -}}

{{/*
Return the Redis secret name
*/}}
{{- define "castled.redis.secretName" -}}
{{- if .Values.redis.enabled }}
    {{- if .Values.redis.auth.existingSecret }}
        {{- printf "%s" .Values.redis.auth.existingSecret -}}
    {{- else -}}
        {{- printf "%s" (include "castled.redis.fullname" .) -}}
    {{- end -}}
{{- else if .Values.externalRedis.existingSecret }}
    {{- printf "%s" .Values.externalRedis.existingSecret -}}
{{- else -}}
    {{- printf "%s-external" (include "castled.redis.fullname" .) -}}
{{- end -}}
{{- end -}}

{{/*
Return the Redis secret key
*/}}
{{- define "castled.redis.secretPasswordKey" -}}
{{- if and .Values.redis.enabled .Values.redis.auth.existingSecret }}
    {{- required "You need to provide existingSecretPasswordKey when an existingSecret is specified in redis" .Values.redis.auth.existingSecretPasswordKey | printf "%s" }}
{{- else if and (not .Values.redis.enabled) .Values.externalRedis.existingSecret }}
    {{- required "You need to provide existingSecretPasswordKey when an existingSecret is specified in redis" .Values.externalRedis.existingSecretPasswordKey | printf "%s" }}
{{- else -}}
    {{- printf "redis-password" -}}
{{- end -}}
{{- end -}}

{{/*
Return whether Redis uses password authentication or not
*/}}
{{- define "castled.redis.auth.enabled" -}}
{{- if or (and .Values.redis.enabled .Values.redis.auth.enabled) (and (not .Values.redis.enabled) (or .Values.externalRedis.password .Values.externalRedis.existingSecret)) }}
    {{- true -}}
{{- end -}}
{{- end -}}