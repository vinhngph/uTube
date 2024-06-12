import React, { useEffect, useState } from 'react';
import { useLocation } from 'react-router-dom';
import { API } from '../../constants';
import './Search.css';

const Search = () => {
  const [searchResults, setSearchResults] = useState([]);
  const [message, setMessage] = useState('');
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const key = searchParams.get('key');

  useEffect(() => {
    const fetchSearchResults = async () => {
      try {
        const response = await fetch(API + `/api/home/search/results?key=${key}`);
        const data = await response.json();
        if (data.message) {
          setMessage(data.message);
        } else {
          setSearchResults(data.results || []);
        }
      } catch (error) {
        setMessage('An error occurred while fetching the search results.');
      }
    };

    if (key) {
      fetchSearchResults();
    }
  }, [key]);

  return (
    <div className='search-page'>
      <h1>Search Results for "{key}"</h1>
      {message ? (
        <p>{message}</p>
      ) : (
        <div className='results'>
          {searchResults.map(result => (
            <div key={result.id} className='result-item'>
              <h2>{result.name}</h2>
              {/* Display other relevant information about the result */}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Search;
