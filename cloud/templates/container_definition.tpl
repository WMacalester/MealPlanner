[
    {
        "name": "mealplanner-api",
        "image": "${api_repository}",
        "cpu": 256,
        "memory": 512,
        "portMappings": [
            {
                "name": "mealplanner-api-8080-tcp",
                "containerPort": 8080,
                "hostPort": 8080,
                "protocol": "tcp",
                "appProtocol": "http"
            }
        ],
        
        "log_configuration": {
            "log_driver": "awslogs",
            "options": {
                "awslogs-group": "${log_group}",
                "awslogs-region": "${aws_region}",
                "awslogs-stream-prefix": "api"
            }
        },
        "essential": true,
        "environment": [
            {
                "name": "admin.username",
                "value": "${admin_username}"
            },
            {
                "name": "authentication.jwt.secretKey",
                "value": "${secret_key}"
            },
            {
                "name": "admin.password",
                "value": "${admin_password}"
            },
            {
                "name": "SPRING_PROFILES_ACTIVE",
                "value": "${active_profile}"
            }
        ]
    }
]