# Visual Question Answering Android App

<div align="center">

![Python](https://img.shields.io/badge/Python-3.8+-blue.svg)
![Android](https://img.shields.io/badge/Android-Kotlin-green.svg)
![AI](https://img.shields.io/badge/AI-Transformers-orange.svg)
![FastAPI](https://img.shields.io/badge/Backend-FastAPI-teal.svg)

**An intelligent Android app that answers questions about images using AI**

[Features](#-features) • [Architecture](#-architecture) • [Setup](#-setup) 

</div>

##  Features

- ** Camera Integration** - Capture images directly from the app
- ** AI-Powered Answers** - Uses ViLT transformer model for VQA
- ** Voice Input Ready** - Architecture prepared for speech recognition
- ** Real-time Processing** - Fast API backend with async support
- ** Native Android** - Built with Kotlin and modern Android SDK

## Architecture
Android App (Kotlin) → FastAPI Backend → ViLT Model → JSON Response
↓
Camera Capture → Image Processing → Question → AI Answer


## Prerequisites

- Python 3.8+
- Android Studio
- OnePlus Nord 5G (or any Android device)
- WiFi network

## Quick Setup

### Backend Setup
```bash
cd backend
pip install -r requirements.txt
python main.py

Android Setup

    Open android_app in Android Studio

    Build and run on connected device

    Update IP in MainActivity.kt with your computer's IP

## Technical Stack
### Backend

    FastAPI - Modern Python web framework

    PyTorch - Deep learning framework

    Transformers - Hugging Face AI models

    ViLT - Vision-and-Language Transformer model

###Android

    Kotlin - Official Android language

    CameraX - Modern camera API

    HTTPURLConnection - Network requests

    Material Design - Modern UI components

## Usage

    Launch the Android app

    Capture an image using the camera

    Enter your question about the image

    Get instant AI-powered answer

## Example questions:

    "What color is the object?"

    "Is there a person in the image?"

    "What is the main subject?"

    "How many objects are there?"

## API Endpoints
POST /answer
json

Request:
{
  "image": File (JPEG/PNG),
  "question": "What is in this image?"
}

Response:
{
  "answer": "a cat",
  "status": "success",
  "question": "What is in this image?"
}

## Model Performance

    Model: dandelin/vilt-b32-finetuned-vqa

    Training: Fine-tuned on VQAv2 dataset

    Accuracy: ~70% on VQAv2 validation

    Speed: ~1-2 seconds per inference
## Future Enhancements

    Voice question input

    Multiple image support

    Answer confidence scores

    Offline mode capability

    Image gallery integration
