image url을 로컬에 다운받고, cloudinary에 upload하여 여러 transform을 테스트할 수 있는 데모 프로젝트 입니다.

## 사전 셋팅
프로젝트를 받고, 상위 디렉토리에 `downloads` 폴터를 생성
```
mkdir ./downloads
```
상위 디렉토리에 `.env` 파일 생성
```
// Copy and paste your API environment variable
CLOUDINARY_URL=cloudinary://<API_KEY>:<API_SECRET>@<CLOUD_NAME>
```


## 프로젝트 실행
image url을 program arguments로 셋팅 후 Main.kt 실행.
![image](https://github.com/user-attachments/assets/d6815731-7896-4a14-bddb-2bdfcc3b0a27)

