package com.macalester.mealplanner.auth.jwt;

public enum JwtToken {
    ACCESS_TOKEN("access-token", "/"),
    REFRESH_TOKEN("refresh-token", "/api/auth");

    private final String headerName;
    private final String path;

    JwtToken(String headerName, String path){
        this.headerName = headerName;
        this.path = path;
    }

    public String getHeaderName(){
        return headerName;
    }

    public String getPath(){
        return path;
    }
}
