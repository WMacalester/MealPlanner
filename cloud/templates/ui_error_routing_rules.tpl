[
  {
    "Condition": {
      "HttpErrorCodeReturnedEquals": "404"
    },
    "Redirect": {
      "HostName": "${hostname}",
      "ReplaceKeyPrefixWith": "#!/"
    }
  },
  {
    "Condition": {
      "HttpErrorCodeReturnedEquals": "403"
    },
    "Redirect": {
      "HostName": "${hostname}",
      "ReplaceKeyPrefixWith": "#!/"
    }
  }
]