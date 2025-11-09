import requests
import json

def test_multiple_questions():
    questions = [
        "What is in this image?",
        "What color is the background?",
        "How many people are there?",
        "Is this indoors or outdoors?",
        "What is the person doing?"
    ]
    
    with open("../model_test/test_image.jpg", "rb") as image_file:
        for question in questions:
            image_file.seek(0)  # Reset file pointer
            files = {"image": ("test.jpg", image_file, "image/jpeg")}
            data = {"question": question}
            
            response = requests.post("http://localhost:8000/answer", files=files, data=data)
            result = response.json()
            
            print(f" {question}")
            print(f" {result['answer']}")
            print("-" * 40)

if __name__ == "__main__":
    test_multiple_questions()

