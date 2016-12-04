define({ "api": [
  {
    "type": "GET",
    "url": "/api/games",
    "title": "Get all users game scores.",
    "name": "GetGameScores",
    "group": "Game",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "optional": false,
            "field": "email",
            "description": "<p>(Optional) Limits the game scores to this user.</p>"
          },
          {
            "group": "Parameter",
            "optional": false,
            "field": "limit",
            "description": "<p>(Optional) Limit the number of game scores returned.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[{\n    \"email\": {String},\n    \"score\": {Number},\n    \"zombiesKilled\": {Number},\n    \"level\": {Number},\n    \"shotsFired\": {Number}\n},\n...\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "Game"
  },
  {
    "type": "POST",
    "url": "/api/game",
    "title": "Create game record.",
    "name": "PostGame",
    "group": "Game",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "userId",
            "description": "<p>The user's ID that played the game.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "score",
            "description": "<p>The score earned in the game.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "zombiesKilled",
            "description": "<p>The number of zombies killed.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "level",
            "description": "<p>The last level reached.</p>"
          },
          {
            "group": "Parameter",
            "type": "Number",
            "optional": false,
            "field": "shotsFired",
            "description": "<p>The number of shots fired during the game.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "Number",
            "optional": false,
            "field": "gameId",
            "description": "<p>The newly inserted game's ID.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "GameRecord-Created:",
          "content": "HTTP/1.1 201 Created\n{\n    \"gameId\": {Number}\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "Game"
  },
  {
    "type": "GET",
    "url": "/api/leaderboard",
    "title": "Get all users leaderboard scores.",
    "name": "GetLeaderboardStats",
    "group": "Leaderboard",
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[{\n    \"email\": {String},\n    \"totalGames\": {Number},\n    \"totalScore\": {Number},\n    \"highscore\": {Number},\n    \"mostZombiesKilled\": {Number},\n    \"highestLevel\": {Number}\n},\n...\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "Leaderboard"
  },
  {
    "type": "GET",
    "url": "/api/leaderboard/user/:email",
    "title": "Get user leaderboard.",
    "name": "UserLeaderboard",
    "group": "Leaderboard",
    "description": "<p>Gets the leaderboard stats for the given email account.</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email account.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n    \"totalGames\": {number},\n    \"totalScore\": {number},\n    \"highscore\": {number},\n    \"mostZombiesKilled\": {number},\n    \"highestLevel\": {number}\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "Leaderboard"
  },
  {
    "type": "POST",
    "url": "/api/user",
    "title": "Create new user.",
    "name": "CreateUser",
    "group": "User",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email.</p>"
          },
          {
            "group": "Parameter",
            "type": "String",
            "optional": false,
            "field": "pass",
            "description": "<p>User's password.</p>"
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Successful-Creation:",
          "content": "HTTP/1.1 201 Created\n{\n    \"email\": {String}\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "fields": {
        "Error 4xx": [
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "MissingParameters",
            "description": "<p>Some parameters were missing.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "Invalid-Email",
            "description": "<p>Was not a valid email address.</p>"
          },
          {
            "group": "Error 4xx",
            "optional": false,
            "field": "DisplayNameConflict",
            "description": ""
          }
        ]
      },
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        },
        {
          "title": "Invalid-Email",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Invalid email format.\"\n}",
          "type": "json"
        },
        {
          "title": "Email-Conflict",
          "content": "HTTP/1.1 409 Conflict\n{\n    \"error\": \"A user already exists with the email provided.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  },
  {
    "type": "GET",
    "url": "/api/users",
    "title": "Get all users' information.",
    "name": "GetAllUsers",
    "group": "User",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response",
          "content": "HTTP/1.1 200 OK\n{\n    \"email\": {String},\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  },
  {
    "type": "GET",
    "url": "/api/users",
    "title": "Get user's information.",
    "name": "GetUser",
    "group": "User",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "Object[]",
            "optional": false,
            "field": "profile",
            "description": "<p>The user's profile object.</p>"
          },
          {
            "group": "Success 200",
            "type": "String",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email.</p>"
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[{\n    \"email\": {String},\n},\n{\n    ...\n}]",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  },
  {
    "type": "POST",
    "url": "/api/login",
    "title": "Log user in (Email / Password).",
    "name": "Login__Email___Password_",
    "group": "User",
    "description": "<p>Takes the user's email and retrieves the salt from the database to then hash the password with the salt. If this hash matches the stored hash, the user is then logged in.</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "optional": false,
            "field": "email",
            "description": "<p>The user's email</p>"
          },
          {
            "group": "Parameter",
            "optional": false,
            "field": "pass",
            "description": "<p>The user's password</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n    \"email\": \"john@example.com\",\n    \"firstName\": \"John\",\n    \"lastName\": \"Smith\",\n    \"email_verified\": true\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  },
  {
    "type": "POST",
    "url": "/api/login",
    "title": "Log user in (Facebook).",
    "name": "Login__Facebook_",
    "group": "User",
    "description": "<p>This will sign the user in with their Facebook account.</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "optional": false,
            "field": "email",
            "description": "<p>User's Facebook email.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n    \"email\": \"john@facebook.com\",\n    \"firstName\": \"\",\n    \"lastName\": \"\",\n    \"email_verified\": true\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  },
  {
    "type": "POST",
    "url": "/api/login",
    "title": "Log user in (Google).",
    "name": "Login__Google_",
    "group": "User",
    "description": "<p>This will sign the user in with their Google account by verifying the token provided with the Google API client. Once valid, a check is done to see if the Google account has been registered with the app before. If not, an account is created. Account information is returned back.</p>",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "optional": false,
            "field": "id_token",
            "description": "<p>The JWT provided by the Google API client that is going to be validated.</p>"
          }
        ]
      }
    },
    "success": {
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n    \"email\": \"john@example.com\",\n    \"firstName\": \"John\",\n    \"lastName\": \"Smith\",\n    \"email_verified\": true\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Missing-Parameters",
          "content": "HTTP/1.1 400 Bad Request\n{\n    \"error\": \"Some parameters were missing.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.1.0",
    "filename": "./server.js",
    "groupTitle": "User"
  }
] });
