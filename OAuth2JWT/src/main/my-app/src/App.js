import React, { useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, useNavigate, useLocation } from 'react-router-dom';
import './App.css';

function Home() {
    const [greeting, setGreeting] = useState("");

    useEffect(() => {
        const name = localStorage.getItem("name");
        if (name) {
            setGreeting(`${name}님 안녕하세요`);
        }
    }, []);

    const onNaverLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/naver";
    };

    const onGoogleLogin = () => {
        window.location.href = "http://localhost:8080/oauth2/authorization/google";
    };

    const getData = () => {
        // 로컬 스토리지에서 access token 읽기 (이미 "Bearer ..." 형식이라면 그대로 사용)
        const token = localStorage.getItem("access");

        fetch("http://localhost:8080/my", {
            method: "GET",
            credentials: 'include',
            headers: {
                Authorization: token
            },
        })
            .then((res) => res.json())
            .then((data) => {
                alert(data.message);
            })
            .catch((error) => alert("에러 발생: " + error));
    };

    const onLogout = () => {
        fetch("http://localhost:8080/logout", {
            method: "POST",
            credentials: 'include'
        })
            .then((res) => {
                if (res.ok) {
                    localStorage.removeItem("access");
                    localStorage.removeItem("name");
                    alert("로그아웃 성공");
                    window.location.href = "/";
                } else {
                    return res.text().then(text => { throw new Error(text); });
                }
            })
            .catch((error) => alert("로그아웃 에러: " + error));
    };

    return (
        <div className="App">
            {greeting && <h2>{greeting}</h2>}
            <button onClick={onNaverLogin}>NAVER LOGIN</button>
            <button onClick={onGoogleLogin}>GOOGLE LOGIN</button>
            <button onClick={getData}>GET DATA</button>
            <button onClick={onLogout}>LOGOUT</button>
        </div>
    );
}

function ChangeToHeader() {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const nameParam = params.get("name");
        if (nameParam) {
            localStorage.setItem("name", nameParam);
        }

        fetch("http://localhost:8080/change-to-header", {
            method: "POST",
            credentials: "include"
        })
            .then((res) => {
                if (!res.ok) {
                    return res.text().then(text => { throw new Error(text); });
                }
                const authHeader = res.headers.get("Authorization");
                if (authHeader) {
                    localStorage.setItem("access", authHeader);
                } else {
                    throw new Error("Authorization 헤더를 찾을 수 없습니다.");
                }
                return res.json();
            })
            .then(() => {
                navigate("/");
            })
            .catch((error) => {
                alert("Access 토큰 변환 실패: " + error);
                navigate("/");
            });
    }, [location.search, navigate]);

}

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/change-to-header" element={<ChangeToHeader />} />
                <Route path="/" element={<Home />} />
            </Routes>
        </Router>
    );
}

export default App;
