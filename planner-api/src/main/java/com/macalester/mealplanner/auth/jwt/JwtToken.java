package com.macalester.mealplanner.auth.jwt;

public enum JwtToken {
    ACCESS_TOKEN("access-token"),
    REFRESH_TOKEN("refresh-token");

    private final String headerName;

    JwtToken(String headerName){
        this.headerName = headerName;
    }

    public String getHeaderName(){
        return headerName;
    }

}
