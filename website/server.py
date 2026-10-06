#!/usr/bin/env python3
"""
Sentinel AI - Standalone Web Frontend Server
Runs on port 3000 and connects to the FastAPI backend API on http://localhost:8000/api/v1.
"""

import http.server
import socketserver
import os
import sys

PORT = 3000
DIRECTORY = os.path.dirname(os.path.abspath(__file__))

class Handler(http.server.SimpleHTTPRequestHandler):
    def __init__(self, *args, **kwargs):
        super().__init__(*args, directory=DIRECTORY, **kwargs)

    def end_headers(self):
        # Enable CORS and caching headers
        self.send_header("Access-Control-Allow-Origin", "*")
        self.send_header("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
        self.send_header("Access-Control-Allow-Headers", "*")
        self.send_header("Cache-Control", "no-cache, no-store, must-revalidate")
        super().end_headers()

def run():
    port = int(sys.argv[1]) if len(sys.argv) > 1 else PORT
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", port), Handler) as httpd:
        print("=================================================================")
        print(f"[*] Sentinel AI Web Console running at: http://localhost:{port}")
        print("[-] Connected to Backend API at: http://localhost:8000/api/v1")
        print("=================================================================")

        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\nShutting down web server...")
            httpd.server_close()

if __name__ == "__main__":
    run()
