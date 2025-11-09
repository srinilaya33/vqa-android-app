import requests
from PIL import Image
import io

def test_vqa_api():
    # Load the test image
    image_path = "../model_test/test_image.jpg"
    
    with open(image_path, "rb") as image_file:
        files = {"image": ("test.jpg", image_file, "image/jpeg")}
        data = {"question": "What is in this image?"}
        
        response = requests.post("http://localhost:8000/answer", files=files, data=data)
        
        print(" API Test Results:")
        print(f"Status: {response.status_code}")
        print(f"Response: {response.json()}")

if __name__ == "__main__":
    test_vqa_api()