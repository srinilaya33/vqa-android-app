import os
from transformers import ViltProcessor, ViltForQuestionAnswering
from PIL import Image
import time

# Set cache directory to D: drive (or any drive with more space)
os.environ['HF_HOME'] = 'D:/huggingface_cache'
os.environ['TRANSFORMERS_CACHE'] = 'D:/huggingface_cache'

print(" Loading VQA model... (This will take 2-3 minutes first time)")
print(f" Cache location: {os.environ['HF_HOME']}")
start_time = time.time()

try:
    # Load the pre-trained model
    processor = ViltProcessor.from_pretrained("dandelin/vilt-b32-finetuned-vqa")
    model = ViltForQuestionAnswering.from_pretrained("dandelin/vilt-b32-finetuned-vqa")

    print(f"Model loaded in {time.time() - start_time:.2f} seconds")

    # Test with your image
    image_path = "test_image.jpg"
    questions = [
        "What is in this image?",
        "What color is the background?",
        "How many people are there?",
        "Is this indoors or outdoors?"
    ]

    # Open and process the image
    image = Image.open(image_path)
    print(f"\n Testing with image: {image_path}")
    print(f" Image size: {image.size}")
    
    for question in questions:
        print(f"\n Question: {question}")
        
        # Prepare inputs for the model
        encoding = processor(image, question, return_tensors="pt")
        
        # Get the answer
        outputs = model(**encoding)
        logits = outputs.logits
        idx = logits.argmax(-1).item()
        answer = model.config.id2label[idx]
        
        print(f" Answer: {answer}")
        
except Exception as e:
    print(f" Error: {e}")
    print(" Make sure you have a file named 'test_image.jpg' in the same folder")
