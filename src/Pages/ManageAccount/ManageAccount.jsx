import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './ManageAccount.css'; // Ensure this file exists and has the necessary styles
import { API } from '../../constants';

const ManageAccount = () => {
    const [users, setUsers] = useState([]);
    const [staffs, setStaffs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedAccount, setSelectedAccount] = useState(null);
    const [accountDetails, setAccountDetails] = useState(null);
    const [error, setError] = useState('');
    const [role, setRole] = useState(1); // Assuming the role of the logged-in user
    const [message, setMessage] = useState(''); // For success/error messages

    const getCookie = (name) => {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) return parts.pop().split(';').shift();
        return null;
    };

    const getUserIdFromCookie = () => {
        const userCookie = getCookie('user');
        if (userCookie) {
            try {
                const userData = JSON.parse(userCookie);
                return userData.userId;
            } catch (error) {
                console.error('Failed to parse user cookie', error);
                return null;
            }
        }
        return null;
    };

    const userId = getUserIdFromCookie();

    useEffect(() => {
        const fetchAccounts = async () => {
            try {
                const userResponse = await axios.get(API + '/api/accounts/users');
                const staffResponse = await axios.get(API + '/api/accounts/staffs');
                setUsers(userResponse.data);
                setStaffs(staffResponse.data);
            } catch (error) {
                console.error('Error fetching accounts:', error);
                setMessage('An error occurred while fetching accounts.');
            } finally {
                setLoading(false);
            }
        };
        
        fetchAccounts();
    }, []);

    const handleUpdate = async (modifyUserId, modifyRole) => {
        try {
            const response = await axios.put(API + '/api/accounts', null, {
                params: {
                    current_user_id: userId,
                    modify_user_id: modifyUserId,
                    modify_role: modifyRole
                }
            });
            setMessage('Role updated successfully.');
            // Update the user's role in the local state without fetching data again
            const updatedUsers = users.map(user => {
                if (user.userId === modifyUserId) {
                    return { ...user, role: modifyRole };
                }
                return user;
            });
            setUsers(updatedUsers);
        } catch (error) {
            if (error.response) {
                switch (error.response.status) {
                    case 403:
                        setMessage('You do not have permission to update this role.');
                        break;
                    case 404:
                        setMessage('Cannot find role for modified account.');
                        break;
                    case 304:
                        setMessage('Error when updating role.');
                        break;
                    default:
                        setMessage('An error occurred while updating the role.');
                }
            } else {
                setMessage('An error occurred while updating the role.');
            }
        }
    };

    const handleDelete = async (deleteUserId) => {
        try {
            const response = await axios.delete(API + '/api/accounts', {
                params: {
                    current_user_id: userId,
                    delete_user_id: deleteUserId
                }
            });
            setMessage('Account deleted successfully.');
            // Remove the deleted account from the local state without fetching data again
            const updatedUsers = users.filter(user => user.userId !== deleteUserId);
            setUsers(updatedUsers);
        } catch (error) {
            if (error.response) {
                switch (error.response.status) {
                    case 403:
                        setMessage('You do not have permission to delete this account.');
                        break;
                    case 404:
                        setMessage('Cannot find delete role account.');
                        break;
                    case 304:
                        setMessage('Error when deleting account.');
                        break;
                    default:
                        setMessage('An error occurred while deleting the account.');
                }
            } else {
                setMessage('An error occurred while deleting the account.');
            }
        }
    };

    const handleAccountClick = async (userId) => {
        setSelectedAccount(userId);
        try {
            const response = await axios.get(API + '/api/accounts/details', {
                params: { user_id: userId }
            });
            setAccountDetails(response.data);
            setError('');
        } catch (error) {
            if (error.response) {
                if (error.response.status === 404) {
                    setError('User ID not found or something went wrong.');
                } else if (error.response.status === 500) {
                    setError('Internal Server Error. Please try again later.');
                } else {
                    setError('An error occurred while fetching account details.');
                }
            } else {
                setError('An error occurred while fetching account details.');
            }
            setAccountDetails(null);
        }
    };

    const renderAccountDetails = () => {
        if (!selectedAccount) return null;
        return (
            <div className='account-details'>
                <h3>Account Details</h3>
                {error ? (
                    <p className='error'>{error}</p>
                ) : (
                    accountDetails && (
                        <>
                            <p>Username: {accountDetails.user_fullname}</p>
                            <p>Birthday: {accountDetails.user_dob}</p>
                        </>
                    )
                )}
                <button onClick={() => setSelectedAccount(null)}>Close</button>
            </div>
        );
    };

    const filteredAccounts = (accounts) => {
        return accounts.filter(account => {
            if (role === 1) {
                return account.role === 1 || account.role === 2 || account.role === 3;
            } else if (role === 2) {
                return account.role === 2 || account.role === 3;
            }
            return false;
        });
    };

    return (
        <div className='manage-account'>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <h2>Users</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>User ID</th>
                                <th>Username</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredAccounts(users).map(user => (
                                <tr key={user.id}>
                                    <td onClick={() => handleAccountClick(user.userId)}>{user.userId}</td>
                                    <td>{user.username}</td>
                                    <td>
                                        <button onClick={() => handleUpdate(user.userId, '2')}>Update</button>
                                        <button onClick={() => handleDelete(user.userId)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    <h2>Staffs</h2>
                    <table>
                        <thead>
                            <tr>
                                <th>User ID</th>
                                <th>Username</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            {filteredAccounts(staffs).map(staff => (
                                <tr key={staff.id}>
                                    <td onClick={() => handleAccountClick(staff.userId)}>{staff.userId}</td>
                                    <td>{staff.username}</td>
                                    <td>
                                        <button onClick={() => handleDelete(staff.userId)}>Delete</button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>

                    {renderAccountDetails()}
                    {message && <p className='message'>{message}</p>}
                </>
            )}
        </div>
    );
};

export default ManageAccount;
