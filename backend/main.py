from fastapi import FastAPI, File, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from transformers import ViltProcessor, ViltForQuestionAnswering
from PIL import Image
import io
import uvicorn
import os

# Set cache directory to use our existing model
os.environ['HF_HOME'] = 'D:/huggingface_cache'
os.environ['TRANSFORMERS_CACHE'] = 'D:/huggingface_cache'

app = FastAPI(title="VQA Server", version="1.0")

# Allow Android app to connect to this server
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)

# Global variables for model
processor = None
model = None

print(" Loading VQA model from cache...")

# Load the model directly (it should use our cached version)
try:
    processor = ViltProcessor.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
    model = ViltForQuestionAnswering.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
    print(" VQA Model loaded successfully from cache!")
except Exception as e:
    print(f" Error loading model: {e}")
    print(" Make sure the model is downloaded in D:/huggingface_cache")

@app.post("/answer")
async def answer_question(
    image: UploadFile = File(...),
    question: str = Form(...)
):
    if model is None:
        return {"answer": "Model not loaded", "status": "error"}
    
    try:
        # Read the uploaded image
        image_data = await image.read()
        image_pil = Image.open(io.BytesIO(image_data))
        
        # Process with VQA model
        encoding = processor(image_pil, question, return_tensors="pt")
        outputs = model(**encoding)
        logits = outputs.logits
        idx = logits.argmax(-1).item()
        answer = model.config.id2label[idx]
        
        return {
            "answer": answer,
            "status": "success",
            "question": question
        }
        
    except Exception as e:
        return {
            "answer": f"Error: {str(e)}", 
            "status": "error",
            "question": question
        }

@app.get("/")
async def root():
    return {"message": "VQA Server is running! Use POST /answer with image and question."}

@app.get("/health")
async def health_check():
    return {"status": "healthy", "model_loaded": model is not None}

if __name__ == "__main__":
    print(" Starting VQA Server on http://localhost:8000")
    uvicorn.run(app, host="0.0.0.0", port=8000)