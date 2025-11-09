import requests
import json

def test_server():
    # First test if server is running
    try:
        response = requests.get("http://localhost:8000/")
        print("✅ Server is running!")
        print(f"Response: {response.json()}")
    except:
        print("❌ Server is not running yet")

def test_vqa_api():
    # Test the VQA endpoint with an image
    try:
        with open("../model_test/test_image.jpg", "rb") as image_file:
            files = {"image": image_file}
            data = {"question": "What is in this image?"}
            
            response = requests.post(
                "http://localhost:8000/answer",
                files=files,
                data=data
            )
            
            print("✅ API Test Results:")
            print(json.dumps(response.json(), indent=2))
            
    except Exception as e:
        print(f"❌ API Test Failed: {e}")
        print("💡 Make sure you have 'test_image.jpg' in model_test folder")

if __name__ == "__main__":
    test_server()
    print("\n" + "="*50)
    test_vqa_api()