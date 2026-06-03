import sqlite3

conn = sqlite3.connect(r"C:\Users\Kamila.DESKTOP-B95LKMN\Desktop\Study\JavaFXproject\course.beauty-salon\salon.db")
cur = conn.cursor()

with open(r"C:\Users\Kamila.DESKTOP-B95LKMN\Downloads\p13.jpg", "rb") as f:
    photo = f.read()

cur.execute("INSERT OR REPLACE INTO master_photo (employee_id, photo) VALUES (?, ?)", (15, photo))

conn.commit()
conn.close()
print("Готово!")