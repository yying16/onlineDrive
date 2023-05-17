var app = new Vue({
    el: '#app',
    data: {
        username: '未命名',
        contributor: "",
        onlineUser: null,                  //在线用户
        dataList: [],                     //当前页要展示的列表数据
        dialogFormVisible_upload: false,      //"新增"表单是否可见
        dialogFormVisible_check: false,     //"查看"表单是否可见
        formData: {
            fileId: 1,
            remarks: "",
            contributor: "",
            fileName: ""
        },//表单数据
        // rules: {         //表单数据校验规则
        //     fileName: [{required: true, message: '文件名为必填项', trigger: 'blur'}],
        //     contributor: [{required: true, message: '贡献者为必填项', trigger: 'blur'}]
        // },
        pagination: {         //分页相关模型数据
            currentPage: 1,   //当前页码
            pageSize: 10,       //每页显示的记录数
            total: 0,          //总记录数
        },
        search: {            //查找相关数据
            type: "",
            content: ""
        }
    },
    //钩子函数，VUE对象初始化完成后自动执行
    created() {
        this.InitUserInfo()
        //调用查询全部数据的操作
        this.getAllFile();
    },
    methods: {
        //返回所有文件
        getAllFile() {
            axios.get("/index/getAllFile/" + this.pagination.currentPage + "/" + this.pagination.pageSize).then((res) => {
                this.pagination.currentPage = res.data.current;
                this.pagination.total = res.data.total;
                this.dataList = res.data.records;
            });
        },


        //切换页码
        handleCurrentChange(currentPage) {
            //修改页码值为当前选中的页码值
            this.pagination.currentPage = currentPage;
            //执行查询
            this.getAllFile();
        },

        //重置表单
        resetForm() {
            this.formData = {
                fileId: 0,
                remarks: "",
                contributor: "",
                fileName: ""
            };
        },

        //取消
        cancel() {
            this.$message.info("当前操作取消");
            this.resetForm();
            this.closeIFrame();
        },

        //关闭所有对话框
        closeIFrame() {
            this.dialogFormVisible_upload = false;
            this.dialogFormVisible_check = false;
        },

        //弹出"新建"窗口
        openUploadFrame() {
            this.contributor = this.username;
            this.dialogFormVisible_upload = true;
            this.resetForm();
        },
        InitUserInfo() {
            axios.get("/infos/getMyInfo").then((res) => {
                this.onlineUser = res.data;
                this.username = res.data.username
                axios.get("/infos/onlineStatus").then((res) => {
                    if (res.data === 1) { //如果在线状态为1, 那就弹出欢迎框
                        this.$message.success('欢迎回来~' + this.onlineUser.username);
                    }
                });
            });
        },

        //确认添加
        confirmUpload() {
            if (this.beforeUpload(this.file)) {
                let fd = new FormData();
                fd.append('file', this.file);
                fd.append('contributor', this.formData.contributor)
                fd.append('remarks', this.formData.remarks)
                axios.post("/index/toUpload", fd, {'Content-Type': 'multipart/form-data'}).then((res) => {
                    if (res.data) {
                        this.$message.success("文件上传成功!");
                        this.closeIFrame();
                    } else {
                        this.$message.error("文件上传失败!");
                    }
                }).catch((e) => {
                    window.alert(e)
                }).finally(() => {
                    this.getAllFile();
                })
            }
        },

        beforeUpload(file) {
            if (file.type !== "" || file.type != null || file.type !== undefined) {
                const isLt5M = file.size / 1024 / 1024 < 50; //这里做文件大小限制
                if (isLt5M) {//如果小于50M
                    return true;
                } else {
                    this.$message.error('上传文件大小不能超过 50MB!');
                    return false;
                }
            } else {
                return false;
            }
        },
        chooseFile(item) {
            this.$message.success("已选择" + item.file.name)
            this.file = item.file
        },

        //弹出“编辑”窗口
        openCheckFrame(row) {
            // row.id为该行记录在原数据库表中的id
            axios.get("/index/getFileById/" + row.fileId).then((res) => {
                this.dialogFormVisible_check = true;
                this.formData = res.data;
            }).finally(() => {
                this.getAllFile();//重新加载数据
            });
        },

        searchFile() {
            if (this.search.type === "") {
                this.$message.error("请选择搜索类型");
            }
            if (this.search.content === "") {
                this.$message.error("请输入搜索关键字");
            }
            axios.get("/index/searchFile/" + this.pagination.currentPage + "/" + this.pagination.pageSize + "/" + this.search.type + "/" + this.search.content).then((res) => {
                this.pagination.currentPage = res.data.current;
                this.pagination.total = res.data.total;
                this.dataList = res.data.records;
            })
        },

        //下载文件
        downloadFile(row) {
            let a = document.createElement('a')
            a.href = "http://localhost:8080/index/toDownload/" + row.fileName + "/" + row.fileId;
            a.click();
        }
    }
})