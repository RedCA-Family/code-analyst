/*
    Copyright 2021 Samsung SDS

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
 */

var merged = arr.reduce(function(a, b) {
    a.concat(b);
}); // Noncompliant: No return statement

for (;;) { // Noncompliant; end condition omitted
}

function test() {
    var duration = + new Date() - start.time,
        isPastHalf = Number(duration) < 250 && Math.abs(delta.x) > 20 || Math.abs(delta.x) > viewport / 2,
        direction = delta.x < 0;

    if (!isScrolling) {
        if (isPastHalf) {
            if (direction) {
                this.close();
            } else {
                if (this.content.getBoundingClientRect().left > viewport / 2 && pulled === true) {
                    this.close();
                    return;
                }
                this.open();
            }
        } else {
            if (this.content.getBoundingClientRect().left > viewport / 2) {
                if (this.isEmpty(delta) || delta.x > 0) {
                    this.close();
                    return;
                }
                this.open();
                return;
            }
            this.close();
        }
    }
}

function test2() {
    var duration = + new Date() - start.time,
        isPastHalf = Number(duration) < 250 && Math.abs(delta.x) > 20 || Math.abs(delta.x) > viewport / 2,
        direction = delta.x < 0;

    if (!isScrolling) {
        if (isPastHalf) {
            if (direction) {
                this.close();
            } else {
                if (this.content.getBoundingClientRect().left > viewport / 2 && pulled === true) {
                    this.close();
                    return;
                }
                this.open();
            }
        } else {
            if (this.content.getBoundingClientRect().left > viewport / 2) {
                if (this.isEmpty(delta) || delta.x > 0) {
                    this.close();
                    return;
                }
                this.open();
                return;
            }
            this.close();
        }
    }

    var duration = + new Date() - start.time,
        isPastHalf = Number(duration) < 250 && Math.abs(delta.x) > 20 || Math.abs(delta.x) > viewport / 2,
        direction = delta.x < 0;

    if (!isScrolling) {
        if (isPastHalf) {
            if (direction) {
                this.close();
            } else {
                if (this.content.getBoundingClientRect().left > viewport / 2 && pulled === true) {
                    this.close();
                    return;
                }
                this.open();
            }
        } else {
            if (this.content.getBoundingClientRect().left > viewport / 2) {
                if (this.isEmpty(delta) || delta.x > 0) {
                    this.close();
                    return;
                }
                this.open();
                return;
            }
            this.close();
        }
    }
}
