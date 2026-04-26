# Symptosis AI - Intelligent Symptom Analysis System

A comprehensive AI-powered healthcare application for intelligent symptom analysis, risk assessment, and patient management using machine learning and natural language processing.

## 🚀 Features

### Core Functionality
- **Intelligent Symptom Analysis**: Advanced ML models for symptom pattern recognition
- **Risk Assessment**: Automated risk level calculation based on symptoms and patient history
- **Patient Management**: Complete patient record system with trend analysis
- **Real-time Monitoring**: Continuous health monitoring with escalation alerts

### AI & ML Integration
- **Machine Learning Models**: Flask-based ML API for prediction and analysis
- **LLM Integration**: Ollama-powered natural language explanations
- **Trend Analysis**: Historical data analysis for predictive insights

### Technical Stack
- **Backend**: Spring Boot (Java) with REST APIs
- **Frontend**: JavaFX dashboard for intuitive user interface
- **Database**: MySQL for robust data storage
- **ML Engine**: Python Flask API with scikit-learn models
- **LLM**: Ollama integration for AI explanations

## 🏗️ Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   JavaFX        │    │   Spring Boot   │    │   Python Flask  │
│   Frontend      │◄──►│   Backend       │◄──►│   ML API        │
│   (Port 3000)   │    │   (Port 8080)   │    │   (Port 5000)   │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                       │                       │
         └───────────────────────┼───────────────────────┘
                                 │
                    ┌─────────────────┐
                    │     MySQL       │
                    │   Database      │
                    │   (symptosis)   │
                    └─────────────────┘
```

## 📋 Prerequisites

- **Java**: JDK 17 or higher
- **Maven**: 3.6+ (included in project)
- **Python**: 3.8+ for ML components
- **MySQL**: 8.0+ database server
- **Ollama**: For LLM integration (optional)

## 🚀 Quick Start

### 1. Database Setup
```bash
# Start MySQL service
sudo service mysql start

# Create database
mysql -u root -p
CREATE DATABASE symptosis;
```

### 2. Environment Configuration
Create `.env` file in project root:
```env
# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/symptosis
DB_USER=your_username
DB_PASSWORD=your_password

# ML API Configuration
ML_API_URL=http://localhost:5000/predict
ML_TIMEOUT=3000

# LLM Configuration
OLLAMA_BASE_URL=http://localhost:11434
OLLAMA_MODEL=qwen3-coder:480b-cloud
OLLAMA_TIMEOUT=5000

# Risk Calculation Weights
WEIGHT_SEVERITY=0.5
WEIGHT_FREQUENCY=0.3
WEIGHT_DURATION=0.2

# Feature Flags
ENABLE_ML=true
ENABLE_LLM=true
ENABLE_LOGGING=true
```

### 3. Start ML Service
```bash
cd ml-model
pip install -r requirements.txt
python api.py
```

### 4. Start Backend
```bash
cd backend
mvn spring-boot:run
```

### 5. Start Frontend
```bash
cd frontend
mvn javafx:run
```

## 🔧 Development Setup

### Backend Development
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```

### Frontend Development
```bash
cd frontend
mvn clean compile
mvn javafx:run
```

### ML Model Training
```bash
cd ml-model
python train.py
python generate_dataset.py
```

## 📊 API Endpoints

### Patient Management
- `GET /api/patients` - List all patients
- `POST /api/patients` - Create new patient
- `GET /api/patients/{id}` - Get patient details
- `PUT /api/patients/{id}` - Update patient
- `DELETE /api/patients/{id}` - Delete patient

### Symptom Analysis
- `POST /api/symptoms/analyze` - Analyze symptoms
- `GET /api/symptoms/trends/{patientId}` - Get symptom trends
- `POST /api/risk/assess` - Assess risk level

### ML Predictions
- `POST /predict` - Get ML predictions (ML API)

## 🧪 Testing

### Backend Tests
```bash
cd backend
mvn test
```

### Integration Tests
```bash
mvn verify
```

## 📁 Project Structure

```
symptosis-ai/
├── backend/                 # Spring Boot application
│   ├── src/main/java/com/symptosis/backend/
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── model/           # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   └── config/          # Configuration classes
│   └── src/test/            # Unit tests
├── frontend/                # JavaFX application
│   └── src/main/java/com/symptosis/frontend/
├── ml-model/                # Python ML components
│   ├── api.py              # Flask API server
│   ├── model.py            # ML model implementation
│   ├── train.py            # Model training script
│   └── dataset.csv         # Training data
├── sample-outputs/          # Sample API responses
├── logs/                   # Application logs
└── README.md               # This file
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- Built with Spring Boot and JavaFX
- ML models powered by scikit-learn
- LLM integration with Ollama
- Database management with MySQL

## 📞 Support

For questions or support, please open an issue on GitHub or contact the development team.

---

**⚠️ Important**: Never commit sensitive information like passwords, API keys, or environment files to version control. Always use `.env` files and add them to `.gitignore`.