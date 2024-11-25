```markdown
# API Documentation

## Base URL
```
http://localhost:8080
```

## Endpoints

### 1. Get Detailed Statistics

- **URL:** `/detailed-scores`
- **Method:** `GET`
- **Description:** Retrieves detailed statistics of student scores, including various statistical measures.

#### Request
- **Headers:** None
- **Parameters:** None

#### Response
- **Status Code:** `200 OK`
- **Body:**
  ```json
  {
    "averageScores": {
      "toan": 8.5,
      "van": 7.8,
      // ...
    },
    "medianScores": {
      "toan": 8.0,
      "van": 7.5,
      // ...
    },
    "standardDeviation": {
      "toan": 1.2,
      "van": 1.0,
      // ...
    },
    // Additional statistical data
  }
  ```

### 2. Get Average Scores Chart

- **URL:** `/average-scores-chart`
- **Method:** `GET`
- **Description:** Generates a chart representing the average scores of each subject.

#### Request
- **Headers:** None
- **Parameters:** None

#### Response
- **Status Code:** `200 OK`
- **Body:** HTML string containing the rendered chart.

### 3. Get Score Statistics by Subject

- **URL:** `/distribution-by-subject`
- **Method:** `GET`
- **Description:** Retrieves statistical distribution of scores for a specific subject.

#### Request
- **Headers:** None
- **Parameters:**
  | Parameter | Type   | Required | Description                                        |
  |-----------|--------|----------|--------------------------------------------------|
  | subject   | String | Yes      | The subject name ("ngoai_ngu:, "toan","vat_ly",) |

#### Example Request
```
GET /distribution-by-subject?subject=toan
```

#### Response
- **Status Code:** `200 OK`
- **Body:**
  ```json
  {
    "subject": "Toán",
    "average": 8.5,
    "median": 8.0,
    "max": 10.0,
    "min": 5.0,
    "standardDeviation": 1.2,
    // Additional distribution data
  }
  ```

- **Error Responses:**
  - **400 Bad Request:** Missing or invalid `subject` parameter.
    ```json
    {
      "error": "Invalid or missing 'subject' parameter."
    }
    ```

### 4. Get Score Distribution Chart

- **URL:** `/score-distribution-chart`
- **Method:** `GET`
- **Description:** Generates an HTML chart showing the distribution of scores for a specific subject.

#### Request
- **Headers:** None
- **Parameters:**
| Parameter | Type   | Required | Description                                        |
  |-----------|--------|----------|--------------------------------------------------|
  | subject   | String | Yes      | The subject name ("ngoai_ngu:, "toan","vat_ly",) |

#### Example Request
```
GET /score-distribution-chart?subject=toan
```

#### Response
- **Status Code:** `200 OK`
- **Body:** HTML string containing the rendered distribution chart.
  ```html
  <img src="data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..." alt="Score Distribution Chart for Toán">
  ```

- **Error Responses:**
  - **400 Bad Request:** Missing or invalid `subject` parameter.
    ```json
    {
      "error": "Invalid or missing 'subject' parameter."
    }
    ```
## Notes

- Ensure that the `subject` parameter matches one of the predefined subjects in `Subject.java` (e.g., `toan`, `van`, `ngoai_ngu`, etc.).
- The API responses are subject to change as the project evolves.
- For any issues or questions, please refer to the [Contact](#contact) section in the README.

## Contact

For further assistance, please contact:
```
nguyenngoctam0332003@gmail.com
```

---
```