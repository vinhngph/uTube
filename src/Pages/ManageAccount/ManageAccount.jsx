import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { Table, Button, Modal, message as antdMessage } from 'antd';
import './ManageAccount.css'; // Ensure this file exists and has the necessary styles
import { API } from '../../constants';

const ManageAccount = () => {
    const [users, setUsers] = useState([]);
    const [staffs, setStaffs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [selectedAccountDetails, setSelectedAccountDetails] = useState(null);
    const [error, setError] = useState('');
    const [role, setRole] = useState(1); // Assuming the role of the logged-in user
    const [showModal, setShowModal] = useState(false);

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
                antdMessage.error('An error occurred while fetching accounts.');
            } finally {
                setLoading(false);
            }
        };

        fetchAccounts();
    }, []);

    const handleUpdate = async (modifyUserId, modifyRole) => {
        try {
            await axios.put(API + '/api/accounts', null, {
                params: {
                    current_user_id: userId,
                    modify_user_id: modifyUserId,
                    modify_role: modifyRole
                }
            });
            antdMessage.success('Role updated successfully.');
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
                        antdMessage.error('You do not have permission to update this role.');
                        break;
                    case 404:
                        antdMessage.error('Cannot find role for modified account.');
                        break;
                    case 304:
                        antdMessage.error('Error when updating role.');
                        break;
                    default:
                        antdMessage.error('An error occurred while updating the role.');
                }
            } else {
                antdMessage.error('An error occurred while updating the role.');
            }
        }
    };

    const handleDelete = async (deleteUserId) => {
        try {
            await axios.delete(API + '/api/accounts', {
                params: {
                    current_user_id: userId,
                    delete_user_id: deleteUserId
                }
            });
            antdMessage.success('Account deleted successfully.');
            // Remove the deleted account from the local state without fetching data again
            const updatedUsers = users.filter(user => user.userId !== deleteUserId);
            setUsers(updatedUsers);
        } catch (error) {
            if (error.response) {
                switch (error.response.status) {
                    case 403:
                        antdMessage.error('You do not have permission to delete this account.');
                        break;
                    case 404:
                        antdMessage.error('Cannot find delete role account.');
                        break;
                    case 304:
                        antdMessage.error('Error when deleting account.');
                        break;
                    default:
                        antdMessage.error('An error occurred while deleting the account.');
                }
            } else {
                antdMessage.error('An error occurred while deleting the account.');
            }
        }
    };

    const handleAccountClick = async (userId) => {
        try {
            const response = await axios.get(API + '/api/accounts/details', {
                params: { user_id: userId }
            });
            setSelectedAccountDetails(response.data);
            setShowModal(true);
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
            setSelectedAccountDetails(null);
        }
    };

    const handleCloseModal = () => {
        setShowModal(false);
        setSelectedAccountDetails(null);
        setError('');
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

    const columns = [
        {
            title: 'User ID',
            dataIndex: 'userId',
            key: 'userId'
        },
        {
            title: 'Username',
            dataIndex: 'username',
            key: 'username'
        },
        {
            title: 'Actions',
            key: 'actions',
            render: (text, record) => (
                <>
                    <Button type="primary" onClick={() => handleUpdate(record.userId, '2')} style={{ marginRight: 8 }}>Update</Button>
                    <Button type="danger" onClick={() => handleDelete(record.userId)}>Delete</Button>
                </>
            )
        }
    ];

    return (
        <div className='manage-account'>
            {loading ? (
                <p>Loading...</p>
            ) : (
                <>
                    <h2>Users</h2>
                    <Table
                        columns={columns}
                        dataSource={filteredAccounts(users)}
                        rowKey="userId"
                        onRow={(record) => ({
                            onClick: () => handleAccountClick(record.userId)
                        })}
                    />

                    <h2>Staffs</h2>
                    <Table
                        columns={columns}
                        dataSource={filteredAccounts(staffs)}
                        rowKey="userId"
                        onRow={(record) => ({
                            onClick: () => handleAccountClick(record.userId)
                        })}
                    />

                    <Modal
                        visible={showModal}
                        title="Account Details"
                        onCancel={handleCloseModal}
                        footer={null}
                    >
                        {error ? (
                            <p className='error'>{error}</p>
                        ) : (
                            selectedAccountDetails && (
                                <>
                                    <p>Username: {selectedAccountDetails.user_fullname}</p>
                                    <p>Birthday: {selectedAccountDetails.user_dob}</p>
                                </>
                            )
                        )}
                    </Modal>
                </>
            )}
        </div>
    );
};

export default ManageAccount;
