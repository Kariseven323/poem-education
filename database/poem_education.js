/*
 Navicat Premium Dump Script

 Source Server         : localhost_27017
 Source Server Type    : MongoDB
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:27017
 Source Schema         : poem_education

 Target Server Type    : MongoDB
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 07/08/2025 10:37:02
*/


// ----------------------------
// Collection structure for comments
// ----------------------------
db.getCollection("comments").drop();
db.createCollection("comments",{
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: [
                "targetId",
                "targetType",
                "userId",
                "content",
                "status"
            ],
            properties: {
                targetId: {
                    bsonType: "objectId"
                },
                targetType: {
                    bsonType: "string",
                    enum: [
                        "guwen",
                        "creation",
                        "sentence",
                        "writer"
                    ]
                },
                userId: {
                    bsonType: "long"
                },
                content: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 1000
                },
                level: {
                    bsonType: "int",
                    minimum: 1,
                    maximum: 10
                },
                status: {
                    bsonType: "int",
                    enum: [
                        0,
                        1
                    ]
                },
                likeCount: {
                    bsonType: "int",
                    minimum: 0
                },
                replyCount: {
                    bsonType: "int",
                    minimum: 0
                }
            }
        }
    },
    validationLevel: "strict",
    validationAction: "error"
});
db.getCollection("comments").createIndex({
    targetId: Int32("1"),
    targetType: Int32("1")
}, {
    name: "target_compound_1"
});
db.getCollection("comments").createIndex({
    userId: Int32("1")
}, {
    name: "userId_1"
});
db.getCollection("comments").createIndex({
    parentId: Int32("1")
}, {
    name: "parentId_1"
});
db.getCollection("comments").createIndex({
    path: Int32("1")
}, {
    name: "path_1"
});
db.getCollection("comments").createIndex({
    targetId: Int32("1"),
    targetType: Int32("1"),
    createdAt: Int32("-1")
}, {
    name: "target_time_1"
});
db.getCollection("comments").createIndex({
    status: Int32("1"),
    createdAt: Int32("-1")
}, {
    name: "status_time_1"
});

// ----------------------------
// Collection structure for creations
// ----------------------------
db.getCollection("creations").drop();
db.createCollection("creations",{
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: [
                "userId",
                "title",
                "content",
                "status"
            ],
            properties: {
                userId: {
                    bsonType: "long"
                },
                title: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 100
                },
                content: {
                    bsonType: "string",
                    minLength: 1,
                    maxLength: 5000
                },
                style: {
                    bsonType: "string",
                    enum: [
                        "律诗",
                        "绝句",
                        "词",
                        "散文",
                        "现代诗",
                        "其他"
                    ]
                },
                status: {
                    bsonType: "int",
                    enum: [
                        -1,
                        0,
                        1
                    ]
                },
                "aiScore.totalScore": {
                    bsonType: "int",
                    minimum: 0,
                    maximum: 100
                }
            }
        }
    },
    validationLevel: "strict",
    validationAction: "error"
});
db.getCollection("creations").createIndex({
    userId: Int32("1")
}, {
    name: "userId_1"
});
db.getCollection("creations").createIndex({
    status: Int32("1")
}, {
    name: "status_1"
});
db.getCollection("creations").createIndex({
    createdAt: Int32("-1")
}, {
    name: "createdAt_-1"
});
db.getCollection("creations").createIndex({
    "aiScore.totalScore": Int32("-1")
}, {
    name: "totalScore_-1"
});
db.getCollection("creations").createIndex({
    userId: Int32("1"),
    status: Int32("1"),
    createdAt: Int32("-1")
}, {
    name: "user_status_time_1"
});
db.getCollection("creations").createIndex({
    style: Int32("1"),
    status: Int32("1")
}, {
    name: "style_status_1"
});
db.getCollection("creations").createIndex({
    "$**": "text"
}, {
    name: "creations_text_search",
    weights: {
        content: Int32("1"),
        description: Int32("1"),
        title: Int32("1")
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: Int32("3")
});

// ----------------------------
// Collection structure for guwen
// ----------------------------
db.getCollection("guwen").drop();
db.createCollection("guwen");
db.getCollection("guwen").createIndex({
    title: Int32("1")
}, {
    name: "title_1"
});
db.getCollection("guwen").createIndex({
    writer: Int32("1")
}, {
    name: "writer_1"
});
db.getCollection("guwen").createIndex({
    dynasty: Int32("1")
}, {
    name: "dynasty_1"
});
db.getCollection("guwen").createIndex({
    title: Int32("1"),
    writer: Int32("1")
}, {
    name: "title_1_writer_1"
});
db.getCollection("guwen").createIndex({
    dynasty: Int32("1"),
    writer: Int32("1")
}, {
    name: "dynasty_1_writer_1"
});
db.getCollection("guwen").createIndex({
    type: Int32("1")
}, {
    name: "type_1"
});
db.getCollection("guwen").createIndex({
    "$**": "text"
}, {
    name: "text_search_index",
    weights: {
        content: Int32("1"),
        title: Int32("1"),
        writer: Int32("1")
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: Int32("3")
});
db.getCollection("guwen").createIndex({
    writer: Int32("1"),
    dynasty: Int32("1"),
    title: Int32("1")
}, {
    name: "writer_dynasty_title_1"
});

// ----------------------------
// Collection structure for learning_progress
// ----------------------------
db.getCollection("learning_progress").drop();
db.createCollection("learning_progress");
db.getCollection("learning_progress").createIndex({
    userId: Int32("1")
}, {
    name: "userId_1"
});
db.getCollection("learning_progress").createIndex({
    targetId: Int32("1"),
    targetType: Int32("1")
}, {
    name: "target_compound_1"
});
db.getCollection("learning_progress").createIndex({
    userId: Int32("1"),
    targetType: Int32("1")
}, {
    name: "user_type_1"
});
db.getCollection("learning_progress").createIndex({
    userId: Int32("1"),
    updatedAt: Int32("-1")
}, {
    name: "user_time_1"
});

// ----------------------------
// Collection structure for recommendations
// ----------------------------
db.getCollection("recommendations").drop();
db.createCollection("recommendations");
db.getCollection("recommendations").createIndex({
    userId: Int32("1")
}, {
    name: "userId_1"
});
db.getCollection("recommendations").createIndex({
    targetId: Int32("1"),
    targetType: Int32("1")
}, {
    name: "target_compound_1"
});
db.getCollection("recommendations").createIndex({
    userId: Int32("1"),
    createdAt: Int32("-1")
}, {
    name: "user_time_1"
});
db.getCollection("recommendations").createIndex({
    algorithm: Int32("1"),
    createdAt: Int32("-1")
}, {
    name: "algorithm_time_1"
});

// ----------------------------
// Collection structure for sentences
// ----------------------------
db.getCollection("sentences").drop();
db.createCollection("sentences");
db.getCollection("sentences").createIndex({
    name: Int32("1")
}, {
    name: "name_1"
});
db.getCollection("sentences").createIndex({
    from: Int32("1")
}, {
    name: "from_1"
});
db.getCollection("sentences").createIndex({
    "$**": "text"
}, {
    name: "sentences_text_search",
    weights: {
        from: Int32("1"),
        name: Int32("1")
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: Int32("3")
});

// ----------------------------
// Collection structure for system.views
// ----------------------------
db.getCollection("system.views").drop();
db.createCollection("system.views");

// ----------------------------
// Collection structure for writers
// ----------------------------
db.getCollection("writers").drop();
db.createCollection("writers");
db.getCollection("writers").createIndex({
    "$**": "text"
}, {
    name: "writers_text_search",
    weights: {
        name: Int32("1"),
        simpleIntro: Int32("1")
    },
    default_language: "english",
    language_override: "language",
    textIndexVersion: Int32("3")
});

// ----------------------------
// View structure for hot_guwen
// ----------------------------
db.getCollection("hot_guwen").drop();
db.createView("hot_guwen","guwen",[
    {
        $lookup: {
            from: "writers",
            localField: "writer",
            foreignField: "name",
            as: "writerInfo"
        }
    },
    {
        $addFields: {
            writerDetail: {
                $arrayElemAt: [
                    "$writerInfo",
                    0
                ]
            }
        }
    },
    {
        $project: {
            title: 1,
            dynasty: 1,
            writer: 1,
            content: 1,
            type: 1,
            remark: 1,
            shangxi: 1,
            translation: 1,
            audioUrl: 1,
            "writerDetail.headImageUrl": 1,
            "writerDetail.simpleIntro": 1
        }
    }
]);

// ----------------------------
// View structure for writer_stats
// ----------------------------
db.getCollection("writer_stats").drop();
db.createView("writer_stats","guwen",[
    {
        $group: {
            _id: "$writer",
            dynasty: {
                $first: "$dynasty"
            },
            totalWorks: {
                $sum: 1
            },
            types: {
                $addToSet: "$type"
            },
            works: {
                $push: {
                    title: "$title",
                    type: "$type"
                }
            }
        }
    },
    {
        $lookup: {
            from: "writers",
            localField: "_id",
            foreignField: "name",
            as: "writerInfo"
        }
    },
    {
        $addFields: {
            writerDetail: {
                $arrayElemAt: [
                    "$writerInfo",
                    0
                ]
            }
        }
    },
    {
        $project: {
            writer: "$_id",
            dynasty: 1,
            totalWorks: 1,
            types: 1,
            works: {
                $slice: [
                    "$works",
                    10
                ]
            },
            "writerDetail.headImageUrl": 1,
            "writerDetail.simpleIntro": 1,
            "writerDetail.detailIntro": 1
        }
    },
    {
        $sort: {
            totalWorks: -1
        }
    }
]);
