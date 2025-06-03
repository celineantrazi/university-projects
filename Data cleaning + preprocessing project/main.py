import pandas as pd
import re


df_og = pd.read_csv("ai3.csv")
df = df_og.copy()

print(df.info())
print(df.head())

missing_values = df.isnull().sum()
missing_percentage = (missing_values / len(df)) * 100
print(pd.DataFrame({"Missing Values": missing_values, "Percentage": missing_percentage}))

def extract_year(year_str):
    if pd.isna(year_str):
        return None
    match = re.search(r'\d{4}', str(year_str))
    if match:
        return int(match.group(0))
    return None

df['year'] = df['year'].apply(extract_year)
df['year'] = df['year'].fillna(df['year'].mode()[0])
df['year'] = df['year'].astype(int)

df['certificate'] = df['certificate'].fillna('Unknown')

df['genre'] = df['genre'].fillna(df['genre'].mode()[0])

df['duration'] = df['duration'].str.replace(' min', '').astype(float)
genre_duration_mean = df.groupby('genre')['duration'].mean()
def fill_missing_duration(row):
    if pd.isnull(row['duration']):
        genre_mean = genre_duration_mean.get(row['genre'], df['duration'].mean())
        return genre_mean if not pd.isnull(genre_mean) else df['duration'].mean()
    return row['duration']
df['duration'] = df.apply(fill_missing_duration, axis=1)

df['rating'] = df['rating'].fillna(df['rating'].mean())

df['votes'] = df['votes'].str.replace(',', '').astype(float)
df['votes'] = df['votes'].fillna(0)

