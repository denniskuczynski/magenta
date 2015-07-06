module.exports = function(grunt) {
    'use strict';

    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        clean: {
            assets: {
                src: [
                    'dist'
                ]
            }
        },

        browserify: {
            app: {
                files: {
                    'dist/app.js': [
                        'src/main/js/app.js'
                    ]
                },
                options: {
                    transform: ['browserify-shim', 'babelify']
                }
            }
        },

        copy: {
            dist: {
                cwd: 'dist',
                expand: true,
                src: '**',
                dest: 'src/main/webapp/static/'
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-clean');
    grunt.loadNpmTasks('grunt-contrib-copy');
    grunt.loadNpmTasks('grunt-browserify');

    grunt.registerTask('js', ['browserify', 'copy']);
    
    grunt.registerTask('default', ['js']);
};
