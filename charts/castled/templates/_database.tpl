{{/*
Add environment variables to configure database values
*/}}

{{- define "castled.database.fullname" -}}
{{- if .Values.mysql.fullnameOverride -}}
{{- .Values.mysql.fullnameOverride | trunc 63 | trimSuffix "-" -}}
{{- else if .Values.mysql.nameOverride -}}
{{- printf "%s-%s" .Release.Name .Values.mysql.nameOverride | trunc 63 | trimSuffix "-" -}}
{{- else -}}
{{- printf "%s-%s" (include "castled.fullname" .) "mysql" | trunc 63 | trimSuffix "-" -}}
{{- end -}}
{{- end -}}

{{/*
Get the mysql credentials secret.
*/}}
{{- define "castled.database.secretName" -}}
{{- if and (.Values.mysql.enabled) (not .Values.mysql.existingSecret) -}}
    {{- printf "%s" (include "castled.mysql.fullname" .) -}}
{{- else if and (.Values.mysql.enabled) (.Values.mysql.existingSecret) -}}
    {{- printf "%s" .Values.mysql.existingSecret -}}
{{- else }}
    {{- if .Values.externalDatabase.existingSecret -}}
        {{- printf "%s" .Values.externalDatabase.existingSecret -}}
    {{- else -}}
        {{ printf "%s-%s" .Release.Name "externaldb" }}
    {{- end -}}
{{- end -}}
{{- end -}}

{{- define "castled.database.host" -}}
{{- ternary (include "castled.database.fullname" .) .Values.externalDatabase.host .Values.mysql.enabled -}}
{{- end -}}

{{- define "castled.database.user" -}}
{{- ternary .Values.mysql.auth.username .Values.externalDatabase.user .Values.mysql.enabled -}}
{{- end -}}

{{- define "castled.database.name" -}}
{{- ternary .Values.mysql.auth.database .Values.externalDatabase.database .Values.mysql.enabled -}}
{{- end -}}

{{- define "castled.database.existingsecret.key" -}}
{{- if .Values.mysql.enabled -}}
    {{- printf "%s" "mysql-password" -}}
{{- else -}}
    {{- if .Values.externalDatabase.existingSecret -}}
        {{- if .Values.externalDatabase.existingSecretPasswordKey -}}
            {{- printf "%s" .Values.externalDatabase.existingSecretPasswordKey -}}
        {{- else -}}
            {{- printf "%s" "db-password" -}}
        {{- end -}}
    {{- else -}}
        {{- printf "%s" "db-password" -}}
    {{- end -}}
{{- end -}}
{{- end -}}

{{- define "castled.database.port" -}}
{{- ternary "3306" .Values.externalDatabase.port .Values.mysql.enabled -}}
{{- end -}}

{{- define "castled.database.password" -}}
{{- ternary .Values.mysql.auth.password .Values.externalDatabase.password .Values.mysql.enabled -}}
{{- end -}}

{{/*
Add environment variables to configure database values
*/}}
{{- define "castled.database.url" -}}
{{- $host := (include "castled.database.host" .) -}}
{{- $dbName := (include "castled.database.name" .) -}}
{{- $port := (include "castled.database.port" . ) -}}
{{- $user := (include "castled.database.user" . ) -}}
{{- $pwd := (include "castled.database.password" . ) -}}
{{- printf "jdbc:mysql://%s:%s/%s?autoReconnect=true&user=%s&password=%s&useSSL=false&serverTimezone=UTC" $host $port $dbName $user $pwd -}}
{{- end -}}